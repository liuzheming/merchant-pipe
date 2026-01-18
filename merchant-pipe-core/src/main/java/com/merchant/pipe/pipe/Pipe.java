package com.merchant.pipe.pipe;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.merchant.kernel.common.exception.ServiceException;
import com.merchant.kernel.common.utils.MsgBuilder;
import com.merchant.kernel.common.utils.RedisKeyUtils;
import com.merchant.kernel.pipe.ActionException;
import com.merchant.kernel.pipe.action.ActionRepo;
import com.merchant.kernel.pipe.action.IActionFactory;
import com.merchant.kernel.pipe.pipe.def.PipeDef;
import com.merchant.kernel.pipe.stage.Stage;
import com.merchant.kernel.pipe.stage.StageDef;
import com.merchant.kernel.pipe.stage.StageFactory;
import com.merchant.kernel.rpc.sdk.SeqGenerateRpc;
import com.merchant.kernel.trigger.ITriggerCenter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.merchant.kernel.pipe.action.AbsAction.LOCK_NAME;
import static com.merchant.kernel.pipe.pipe.PipeStatusEnum.*;
import static com.merchant.kernel.trigger.TriggerEventType.STAGE_START_EVENT;

/**
 * Create on 2022/12/4
 */
@Data
@Slf4j
public class Pipe implements IPipe {

    private Long id;
    private String name;
    private String code;
    private Long taskId;
    private Long processId;
    private PipeTypeEnum pipeType;
    private PipeContext pipeContext;
    private String headStageNo;
    private String tailStageNo;
    private PipeStatusEnum status;
    private LocalDateTime triggerTime;
    private LocalDateTime doneTime;
    private LocalDateTime cTime;

    private String pipeOrganizer;
    @Resource
    private ActionRepo actionRepo;
    @Resource
    private IActionFactory actionFactory;
    @Resource
    private PipeRepo pipeRepo;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private SeqGenerateRpc seqGenerateRpc;
    @Resource
    private StageFactory stageFactory;
    @Resource
    private ITriggerCenter triggerCenter;

    @Override
    public void init(String pipeName, PipeDef pipeDef,
                     PipeContext pipeContext, LocalDateTime triggerTime) throws ClassNotFoundException {
        // create stage
        if (CollectionUtils.isEmpty(pipeDef.getStageDefs())) {
            String msg = MsgBuilder.build("StageDefs can not be null, pipeDefId:{}, pipeDefName:{}, pipeDefCode:{}",
                pipeDef.getId(), pipeDef.getName(), pipeDef.getCode());
            throw new ServiceException(msg);
        }
        List<Stage> stages = createStage(pipeContext, pipeDef.getStageDefs());
        headStageNo = stages.get(0).getStageNo();
        tailStageNo = stages.get(stages.size() - 1).getStageNo();
        status = PENDING;
        PipeTypeEnum currentPipeType = pipeContext.getPipeType();
        this.setPipeType(currentPipeType);
        pipeRepo.update(this);
    }

    @Override
    public void init(PipeDef pipeDef) throws ClassNotFoundException {
        // create stage
        List<Stage> stages = createStage(new PipeContext(), pipeDef.getStageDefs());
        headStageNo = stages.get(0).getStageNo();
        tailStageNo = stages.get(stages.size() - 1).getStageNo();
        status = RUNNING;
        pipeRepo.update(this);
    }


    private List<Stage> createStage(PipeContext pipeContext, List<StageDef> stageDefs) throws ClassNotFoundException {
//        String nextStageNo = StringUtils.EMPTY;
        List<Stage> stages = new ArrayList<>(stageDefs.size());
        List<String> stageNos = new ArrayList<>(stageDefs.size() + 1);
        // 生成 stageNos
        for (int i = 0; i < stageDefs.size(); i++) {
            stageNos.add(seqGenerateRpc.generate());
        }
        // 补充 null 占位，防止数组越界
        stageNos.add(null);
        for (int idx = 0; idx < stageDefs.size(); idx++) {
            StageDef stageDef = stageDefs.get(idx);
            // create stages
            String stageNo = stageNos.get(idx);
            String nextStageNo = stageNos.get(idx + 1);
            Stage stage = stageFactory.create(getId(), stageNo, nextStageNo, stageDef, pipeContext);
            stages.add(stage);
        }
        return stages;
    }

    @Override
    public Result startup() {
        if (status == DONE) {
            return new Result(true);
        }
        if (status == RUNNING) {
            String msg = MsgBuilder.build("pipe is already running, can not start again, pipeId:{}", id);
            throw new ServiceException(msg);
        }
        triggerCenter.register(LocalDateTime.now(), STAGE_START_EVENT, headStageNo, null, null);
        pipeRepo.updateStatus(this, RUNNING);
        return new Result(true);
    }


    @Override
    public Result startupImmediately() {
        if (status == DONE) {
            return new Result(true);
        }
        if (status == RUNNING) {
            String msg = MsgBuilder.build("pipe is already running, can not start again, pipeId:{}", id);
            throw new ServiceException(msg);
        }
        pipeRepo.updatePipeStatus(this, RUNNING);
        triggerCenter.register(LocalDateTime.now(), STAGE_START_EVENT, headStageNo, null, null);
        return new Result(true);
    }


    @Override
    public Result restart() {
        triggerCenter.register(LocalDateTime.now(), STAGE_START_EVENT, headStageNo, null, null);
        return new Result(true);
    }

    /**
     * TODO - lzm
     */
    @Override
    public Boolean shutdown() {
        String lockKey = RedisKeyUtils.getKey(LOCK_NAME, String.valueOf(id));
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            boolean locked = rLock.tryLock(3, 300, TimeUnit.SECONDS);
            if (!locked) {
                LOGGER.warn("action list execute 获取锁失败!");
                return false;
            }
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new ActionException(e);
        } catch (Exception e) {
            throw new ActionException("shutdown pipe failed, pipeId:" + id, e);
        } finally {
            if (rLock.isHeldByCurrentThread() && rLock.isLocked()) {
                rLock.unlock();
            }
        }
        return true;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void signal(String stageNo) {
        if (!getTailStageNo().equals(stageNo)) {
            LOGGER.error("receive a fail signal from pipeId:{}, stageNo:{}", id, stageNo);
            return;
        }
        LOGGER.info("pipe receive signal from stage, will notify client, pipeId:{}, stageNo:{}", id, stageNo);
        pipeRepo.done(this);
    }


    @Override
    public void complete() {
        LOGGER.info("pipe will done， pipeId :{}", id);
        pipeRepo.done(this);

    }

    public void refreshContext(String actionNo, ObjectNode actionOutput) {
        if (actionOutput != null) {
            getPipeContext().getPipeData().setAll(actionOutput);
        }
        pipeRepo.update(this);
    }


    public PipeStatusEnum status() {
        return status;
    }

    public boolean checkDone() {
        return DONE.equals(this.status);
    }

    public PipeContext getPipeContext() {
        return pipeContext;
    }
}
