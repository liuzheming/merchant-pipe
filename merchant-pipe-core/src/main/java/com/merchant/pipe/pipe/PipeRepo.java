package com.merchant.pipe.pipe;

import com.merchant.kernel.common.utils.BeanCopyUtils;
import com.merchant.kernel.common.utils.CheckUtil;
import com.merchant.kernel.common.utils.DateUtil;
import com.merchant.kernel.common.utils.JsonUtils;
import com.merchant.kernel.pipe.db.tables.records.ProcessActionPipeRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.merchant.kernel.pipe.db.Tables.PROCESS_ACTION_PIPE;
import static com.merchant.kernel.pipe.pipe.PipeStatusEnum.DONE;


/**
 * Create on 2022/2/18
 */
@Slf4j
@Component
public class PipeRepo {

    @Autowired
    private DSLContext dslContext;


    public List<ProcessActionPipeRecord> getByTaskId(Long taskId) {
        List<ProcessActionPipeRecord> list = dslContext.select().from(PROCESS_ACTION_PIPE)
            .where(PROCESS_ACTION_PIPE.TASK_ID.eq(taskId))
            .fetchInto(ProcessActionPipeRecord.class);
        return list;
    }

    public List<ProcessActionPipeRecord> getByProcessId(Long processId) {
        List<ProcessActionPipeRecord> list = dslContext.select().from(PROCESS_ACTION_PIPE)
            .where(PROCESS_ACTION_PIPE.PROCESS_ID.eq(processId))
            .fetchInto(ProcessActionPipeRecord.class);
        return list;
    }

    public ProcessActionPipeRecord get(Long groupId) {
        Optional<ProcessActionPipeRecord> recordOptional = dslContext.select().from(PROCESS_ACTION_PIPE)
            .where(PROCESS_ACTION_PIPE.ID.eq(groupId))
            .fetchOptionalInto(ProcessActionPipeRecord.class);
        return recordOptional.orElse(null);
    }

    public ProcessActionPipeRecord create(String name, Long taskId, Long processId, PipeContext pipeContext,
                                          LocalDateTime triggerTime) {

        ProcessActionPipeRecord record = new ProcessActionPipeRecord();
        record.setContext("{}");

        if (StringUtils.isNotEmpty(name)) {
            record.setName(name);
        }
        if (null != taskId) {
            record.setTaskId(taskId);
        }
        if (null != processId) {
            record.setProcessId(processId);
        }
        if (null != pipeContext) {
            record.setContext(JsonUtils.obj2Str(pipeContext));
        }
        record.setStatus(PipeStatusEnum.CREATED.status);
        if (null == triggerTime) {
            triggerTime = DateUtil.DEFAULT_START_TIME;
        }
        record.setTriggerTime(triggerTime);
        record.setCtime(LocalDateTime.now());
        record.setMtime(LocalDateTime.now());
        ProcessActionPipeRecord recordWithId = dslContext.insertInto(PROCESS_ACTION_PIPE)
            .set(record).returning(PROCESS_ACTION_PIPE.ID).fetchOne();
        record.setId(recordWithId.getId());
        return record;

    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(Pipe pipe, PipeStatusEnum status) {
        pipe.setStatus(status);
        update(pipe);
    }


    // 与上面区分事物隔离机制
    public void updatePipeStatus(Pipe pipe, PipeStatusEnum status) {
        pipe.setStatus(status);
        update(pipe);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void done(Pipe pipe) {
        pipe.setStatus(DONE);
        pipe.setDoneTime(LocalDateTime.now());
        update(pipe);
    }

    public int update(Pipe pipe) {
        ProcessActionPipeRecord record = BeanCopyUtils.build(pipe, ProcessActionPipeRecord.class, true);
        record.setStatus(pipe.getStatus().status);
        record.setContext(JsonUtils.obj2Str(pipe.getPipeContext()));
        int i = dslContext.update(PROCESS_ACTION_PIPE)
            .set(record).where(PROCESS_ACTION_PIPE.ID.eq(pipe.getId())).execute();
        CheckUtil.isTrue(i == 1, "update actionGroup fail");
        return i;
    }

    /**
     * 双层转换，目的是为了将空格排除出去，并且可以校验是否有重复人员
     *
     * @param pipeId
     * @param pipeOrganizerList
     */
    public void setPipeOrganizer(Long pipeId, List<String> pipeOrganizerList) {
        if (CollectionUtils.isEmpty(pipeOrganizerList)) {
            return;
        }

        ProcessActionPipeRecord record = this.get(pipeId);
        // pipeOrganizerList 转化为用逗号分割
        String pipeOrganizer = String.join(",", pipeOrganizerList);
        record.setPipeOrganizer(pipeOrganizer);
        dslContext.update(PROCESS_ACTION_PIPE)
            .set(record).where(PROCESS_ACTION_PIPE.ID.eq(pipeId)).execute();
    }

}
