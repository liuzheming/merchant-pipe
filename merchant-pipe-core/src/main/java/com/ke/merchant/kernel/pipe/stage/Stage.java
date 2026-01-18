package com.merchant.pipe.stage;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.merchant.common.exception.ServiceException;
import com.merchant.common.utils.BeanCopyUtils;
import com.merchant.common.utils.MsgBuilder;
import com.merchant.pipe.action.*;
import com.merchant.pipe.facade.IActionFacade;
import com.merchant.pipe.pipe.IPipe;
import com.merchant.pipe.pipe.IPipeFactory;
import com.merchant.pipe.pipe.PipeContext;
import com.merchant.pipe.pipe.Result;
import com.merchant.trigger.ITriggerCenter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static com.merchant.trigger.TriggerEventType.ACTION_START_EVENT;
import static com.merchant.trigger.TriggerEventType.STAGE_START_EVENT;

/**
 * Create on 2023/5/1
 */
@Slf4j
public class Stage implements IStageLifeCycle, IStage {

    private Long id;

    private String stageNo;

    private String nextStageNo;

    private Long pipeId;

    private String status;

    private LocalDateTime doneTime;

    @Resource
    private IActionFactory actionFactory;
    @Resource
    private ITriggerCenter triggerCenter;
    @Resource
    private StageRepo stageRepo;
    @Resource
    private IPipeFactory pipeFactory;
    @Resource
    private IActionFacade actionFacade;


    @Override
    public void init(Long pipeId, String nextStageNo, StageDef stageDef, PipeContext pipeContext)
        throws ClassNotFoundException {
        List<ActionDef> parallelActionDefs = stageDef.getActionDefs();
        for (ActionDef actionDef : parallelActionDefs) {
            if (pipeContext.getForceExecMode() != null) {
                actionDef.setExecMode(pipeContext.getForceExecMode());
            }
            if (null != actionDef.getFormDefId()) {
                ActionContext actionContext = BeanCopyUtils.build(pipeContext, ActionContext.class);
                actionContext.getExtData().putAll(actionDef.getExtData());
                actionFactory.createAction4CustomTask(pipeId, getStageNo(), actionDef, actionContext);
            } else {
                ActionContext actionContext = BeanCopyUtils.build(pipeContext, ActionContext.class);
                actionContext.getExtData().putAll(actionDef.getExtData());
                actionFactory.createAction(pipeId, getStageNo(), actionDef, actionContext);
            }
        }
        this.status = StageStatusEnum.PENDING.status;
    }

    @Override
    public Result startup() {
        if (StageStatusEnum.DONE.status.equals(status)) {
            return new Result(true);
        }
        if (StageStatusEnum.RUNNING.status.equals(status)) {
            String msg = MsgBuilder.build("stage is already running, can not start again, stageNo:{}", stageNo);
            throw new ServiceException(msg);
        }
        List<IAction> actions = actionFactory.loadActionByStageNo(stageNo);
        for (IAction action : actions) {
            triggerCenter.register(LocalDateTime.now(), ACTION_START_EVENT,
                action.getItemNo(), null, null);
        }
        status = StageStatusEnum.RUNNING.status;
        stageRepo.update(this);
        return new Result(true);
    }

    @Override
    public Result tryCompleteAllAction(Boolean force) {
        if (StageStatusEnum.DONE.status.equals(status)) {
            return new Result(true);
        }
        if (StageStatusEnum.RUNNING.status.equals(status)) {
            String msg = MsgBuilder.build("stage is already running, can not start again, stageNo:{}", stageNo);
            throw new ServiceException(msg);
        }
        status = StageStatusEnum.RUNNING.status;
        stageRepo.update(this);

        List<IAction> actions = actionFactory.loadActionByStageNo(stageNo);
        for (IAction action : actions) {
            try {
                Result result = actionFacade.startAction(action.getItemNo());
                Boolean success = result.getSuccess();
                if (!success) {
                    LOGGER.info("run action failed, actionNo:{}", action.getItemNo());
                    triggerCenter.register(LocalDateTime.now(), ACTION_START_EVENT,
                        action.getItemNo(), null, null);
                }
            } catch (Exception e) {
                LOGGER.info("start action failed, actionNo:{}", action.getItemNo(), e);
                triggerCenter.register(LocalDateTime.now(), ACTION_START_EVENT,
                    action.getItemNo(), null, null);
            }
        }
        return new Result(true);
    }

    @Override
    public void shutdown() {

    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getStageNo() {
        return stageNo;
    }

    @Override
    public void signal(String actionNo, ObjectNode actionOutput) {
        LOGGER.info("stage receive signal from action, stageNo:{} actionNo:{}", stageNo, actionNo);
        IPipe pipe = pipeFactory.get(pipeId);
        pipe.refreshContext(actionNo, actionOutput);
        List<IAction> actions = actionFactory.loadActionByStageNo(stageNo);
        boolean allActionDone = true;
        for (IAction action : actions) {
            allActionDone = allActionDone && action.done();
        }
        if (allActionDone) {
            status = StageStatusEnum.DONE.status;
            doneTime = LocalDateTime.now();
            stageRepo.update(this);
            if (StringUtils.isNotEmpty(getNextStageNo())) {
                LOGGER.info("stage has a next node, register a trigger for it, stageNo:{}, nextStageNo:{}",
                    getStageNo(), getNextStageNo());
                if (StageStatusEnum.CREATED.status.equals(stageRepo.get(getNextStageNo()).getStatus())) {

                    triggerCenter.register(
                        LocalDateTime.now(), STAGE_START_EVENT, getNextStageNo(), null, null);
                }
            } else {
                LOGGER.info("stage has no next node, will signal pipe, stageNo:{}, pipeId:{}", stageNo, pipeId);
                pipe.signal(stageNo);
            }
        }
    }


    @Override
    public void triggerNextStageActionList(String actionNo) {
        LOGGER.info("stage receive signal from action, stageNo:{} actionNo:{}", stageNo, actionNo);
        IPipe pipe = pipeFactory.get(pipeId);
        List<IAction> actions = actionFactory.loadActionByStageNo(stageNo);
        boolean allActionDone = true;
        for (IAction action : actions) {
            allActionDone = allActionDone && action.done();
        }
        if (allActionDone) {
            status = StageStatusEnum.DONE.status;
            doneTime = LocalDateTime.now();
            stageRepo.update(this);
            if (StringUtils.isNotEmpty(getNextStageNo())) {
                LOGGER.info("stage has a next node, register a trigger for it, stageNo:{}, nextStageNo:{}",
                    getStageNo(), getNextStageNo());
                if (StageStatusEnum.CREATED.status.equals(stageRepo.get(getNextStageNo()).getStatus())) {
                    triggerCenter.register(LocalDateTime.now(), STAGE_START_EVENT, getNextStageNo(), null, null);
                }
            } else {
                LOGGER.info("stage has no next node, will signal pipe, stageNo:{}, pipeId:{}", stageNo, pipeId);
                pipe.signal(stageNo);
            }
        }

    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStageNo(String stageNo) {
        this.stageNo = stageNo;
    }

    public String getNextStageNo() {
        return nextStageNo;
    }

    public void setNextStageNo(String nextStageNo) {
        this.nextStageNo = nextStageNo;
    }

    public Long getPipeId() {
        return pipeId;
    }

    public void setPipeId(Long pipeId) {
        this.pipeId = pipeId;
    }

    public LocalDateTime getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(LocalDateTime doneTime) {
        this.doneTime = doneTime;
    }
}
