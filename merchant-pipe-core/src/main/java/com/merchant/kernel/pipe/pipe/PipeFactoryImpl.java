package com.merchant.kernel.pipe.pipe;

import com.merchant.kernel.common.utils.BeanCopyUtils;
import com.merchant.kernel.common.utils.JsonUtils;
import com.merchant.kernel.pipe.db.tables.records.ProcessActionPipeRecord;
import com.merchant.kernel.pipe.pipe.def.PipeDef;
import com.merchant.kernel.trigger.ITriggerCenter;
import com.merchant.kernel.trigger.TriggerEventType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Create on 2022/12/4
 */
@Slf4j
@Service
public class PipeFactoryImpl implements IPipeFactory {

    @Resource
    private PipeRepo pipeRepo;
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private ITriggerCenter triggerCenter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPipe createPipe(PipeDef pipeDef,
                            PipeContext pipeContext, LocalDateTime triggerTime)
        throws Exception {
        // create pipe
        ProcessActionPipeRecord record = pipeRepo.create(pipeDef.getName(), pipeContext.getTaskId(),
            pipeContext.getProcessId(), pipeContext, triggerTime);
        Pipe pipe = BeanCopyUtils.build(record, Pipe.class);
        pipe.setStatus(PipeStatusEnum.CREATED);
        pipe.setPipeContext(pipeContext);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(pipe);
        pipe.init(pipe.getName(), pipeDef, pipeContext, triggerTime);
        // 如果launchTime为null或者小于等于当前时间
        if (triggerTime == null) {
            return pipe;
        }
        if (!triggerTime.isAfter(LocalDateTime.now())) {
            pipe.startupImmediately();
            return pipe;
        }
        triggerCenter.register(triggerTime, TriggerEventType.PIPE_START_EVENT,
            String.valueOf(pipe.getId()), null, null);
        return pipe;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPipe createPipe4CustomTask(PipeDef pipeDef, PipeContext pipeContext) throws Exception {
        ProcessActionPipeRecord record = pipeRepo.create(pipeDef.getName(), pipeContext.getTaskId(),
            pipeContext.getProcessId(), pipeContext, null);
        Pipe pipe = BeanCopyUtils.build(record, Pipe.class);
        pipe.setStatus(PipeStatusEnum.CREATED);
        pipe.setPipeContext(pipeContext);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(pipe);
        pipe.init(pipeDef.getName(), pipeDef, pipeContext, null);
        return pipe;
    }

    @Override
    public IPipe get(long pipeId) {
        ProcessActionPipeRecord record = pipeRepo.get(pipeId);
        if (null == record) {
            return null;
        }
        Pipe pipe = BeanCopyUtils.build(record, Pipe.class);
        pipe.setStatus(PipeStatusEnum.of(record.getStatus()));
        pipe.setCTime(record.getCtime());
        PipeContext context = JsonUtils.str2Obj(record.getContext(), PipeContext.class);
        pipe.setPipeContext(context);
        if (context != null) {
            pipe.setPipeType(context.getPipeType());
        }
        String pipeOrganizer = record.getPipeOrganizer();
        if (StringUtils.isBlank(pipeOrganizer)) {
            pipe.setPipeOrganizer(context == null ? null : context.getUcid());
        } else {
            pipe.setPipeOrganizer(record.getPipeOrganizer());
        }

        applicationContext.getAutowireCapableBeanFactory().autowireBean(pipe);
        return pipe;
    }

    public List<IPipe> listByTaskId(long taskId) {
        List<ProcessActionPipeRecord> records = pipeRepo.getByTaskId(taskId);
        List<IPipe> groups = new ArrayList<>();
        for (ProcessActionPipeRecord record : records) {
            IPipe pipe = get(record.getId());
            groups.add(pipe);
        }
        return groups;
    }

    @Override
    public List<IPipe> listNotDone(String status) {
        return null;
    }

    @Override
    public void setPipeOrganizer(Long pipeId, List<String> pipeOrganizerList) {
        pipeRepo.setPipeOrganizer(pipeId, pipeOrganizerList);
    }

}
