package com.merchant.pipe.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.merchant.kernel.common.constant.DbConst;
import com.merchant.kernel.common.utils.BeanCopyUtils;
import com.merchant.kernel.common.utils.BizAssertUtils;
import com.merchant.kernel.common.utils.DateUtil;
import com.merchant.kernel.common.utils.JsonUtils;
import com.merchant.kernel.pipe.db.tables.pojos.ProcessAction;
import com.merchant.kernel.pipe.db.tables.records.ProcessActionRecord;
import com.merchant.kernel.enums.ExecModeEnum;
import com.merchant.kernel.pipe.ActionStatusEnum;
import com.merchant.kernel.pipe.action.model.QueryActionReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.merchant.kernel.pipe.db.Tables.PROCESS_ACTION;


/**
 * Create on 2022/2/18
 */
@Slf4j
@Component
public class ActionRepo {

    @Autowired
    private DSLContext dslContext;

    public <P, R> void save(AbsAction<P, R> absAction) {
        LOGGER.info("save action:{}", absAction);
        ProcessActionRecord record = BeanCopyUtils.build(absAction, ProcessActionRecord.class, true);
        record.setCtime(LocalDateTime.now());
        record.setParam(JsonUtils.obj2Str(absAction.getParam()));
        record.setResult(JsonUtils.obj2Str(absAction.getResult()));
        if (StringUtils.isNotEmpty(absAction.getBizExplain())) {
            record.setBizExplain(absAction.getBizExplain());
        }
        if (StringUtils.isNotEmpty(absAction.getErrMsg())) {
            record.setErrMsg(absAction.getErrMsg());
        } else {
            record.setErrMsg(StringUtils.EMPTY);
        }
        record.setTip(StringUtils.defaultString(absAction.getTip()));
        if (null != absAction.getDoneTime()) {
            record.setDoneTime(absAction.getDoneTime());
        }
        record.setContext(JsonUtils.obj2Str(absAction.getContext()));
        if (absAction.getExecMode() != null) {
            record.setExecMode(absAction.getExecMode().getCode());
        }
        record.setStatus(absAction.getStatus().status);
        record.setCtime(LocalDateTime.now());
        record.setMtime(LocalDateTime.now());
        applyRequiredDefaults(record, absAction);
        dslContext.insertInto(PROCESS_ACTION).set(record).execute();
        absAction.setId(record.getId());
    }

    public <P, R> void update(AbsAction<P, R> absAction) {
        LOGGER.info("update action:{}", absAction);
        ProcessActionRecord record = BeanCopyUtils.build(absAction, ProcessActionRecord.class);
        record.setContext(JsonUtils.obj2Str(absAction.getContext()));
        record.setParam(JsonUtils.obj2Str(absAction.getParam()));
        record.setResult(JsonUtils.obj2Str(absAction.getResult()));

        if (StringUtils.isNotEmpty(absAction.getErrMsg())) {
            record.setErrMsg(StringUtils.abbreviate(absAction.getErrMsg(), 4096));
        } else {
            record.setErrMsg(StringUtils.EMPTY);
        }
        record.setTip(StringUtils.defaultString(absAction.getTip()));
        if (StringUtils.isNotEmpty(absAction.getBizExplain())) {
            record.setBizExplain(absAction.getBizExplain());
        }
        if (null != absAction.getDoneTime()) {
            record.setDoneTime(absAction.getDoneTime());
        }
        if (absAction.getExecMode() != null) {
            record.setExecMode(absAction.getExecMode().getCode());
        }
        record.setStatus(absAction.getStatus().status);
        record.setMtime(LocalDateTime.now());
        applyRequiredDefaults(record, absAction);
        dslContext.update(PROCESS_ACTION).set(record)
            .where(PROCESS_ACTION.ID.eq(absAction.getId()))
            .execute();
        absAction.setId(record.getId());
    }

    public int updateContext(String itemNo, JsonNode context) {
        ProcessActionRecord record = dslContext.selectFrom(PROCESS_ACTION)
            .where(PROCESS_ACTION.ITEM_NO.eq(itemNo))
            .fetchOne();
        LOGGER.info("process action context update, itemNo is{}, old context is {}", itemNo, record.getContext());
        return dslContext.update(PROCESS_ACTION).set(PROCESS_ACTION.CONTEXT, JsonUtils.obj2Str(context))
            .where(PROCESS_ACTION.ITEM_NO.eq(itemNo))
            .execute();
    }

    public int update(String itemNo, JsonNode param, JsonNode result, String errMsg,
                      ExecModeEnum execMode, ActionStatusEnum status, String ucid) {
        ProcessActionRecord record = buildRecord(itemNo, param, result, errMsg, execMode, status, ucid, null, null, null, null);
        return dslContext.update(PROCESS_ACTION).set(record)
            .where(PROCESS_ACTION.ITEM_NO.eq(itemNo))
            .and(PROCESS_ACTION.DELETED.eq(DbConst.ALIVE))
            .execute();
    }


    public int updateStatusByStageNo(String stageNo, ActionStatusEnum status) {
        return dslContext.update(PROCESS_ACTION).set(PROCESS_ACTION.STATUS, status.status)
            .where(PROCESS_ACTION.STAGE_NO.eq(stageNo))
            .and(PROCESS_ACTION.DELETED.eq(DbConst.ALIVE))
            .execute();
    }



    public int reset(String itemNo, ExecModeEnum execMode, String status, String ucid) {
        ProcessActionRecord record = new ProcessActionRecord();
        record.setItemNo(itemNo);
        record.setParam(null);
        record.setResult(null);
        record.setErrMsg(StringUtils.EMPTY);
        record.setExecMode(execMode.getCode());
        record.setBizExplain(StringUtils.EMPTY);
        record.setStatus(status);
        if (StringUtils.isNotEmpty(ucid)) {
            record.setUcid(ucid);
        }
        return dslContext.update(PROCESS_ACTION).set(record)
            .where(PROCESS_ACTION.ITEM_NO.eq(itemNo))
            .and(PROCESS_ACTION.DELETED.eq(DbConst.ALIVE))
            .execute();
    }


    public int create(String itemNo, JsonNode param, JsonNode result, String errMsg, ExecModeEnum execMode,
                      ActionStatusEnum status, Long taskId, String taskCode, Long processId, String ucid,
                      String name, String clazzName, String context, Long pipeId, String stageNo, String inputScript) {

        BizAssertUtils.notEmpty(itemNo, "itemNo can not be null");
        BizAssertUtils.notNull(taskId, "taskId can not be null");
        BizAssertUtils.notEmpty(taskCode, "taskCode can not be null");
        BizAssertUtils.notNull(processId, "processId can not be null");

        ProcessActionRecord record = buildRecord(itemNo, param, result, errMsg, execMode, status, ucid, name, clazzName, context, inputScript);

        record.setTaskId(taskId);
        record.setTaskCode(taskCode);
        record.setProcessId(processId);
        record.setPipeId(pipeId);
        record.setStageNo(stageNo);

        return dslContext.insertInto(PROCESS_ACTION).set(record).execute();
    }

    public ProcessAction get(String itemNo) {
        return dslContext.selectFrom(PROCESS_ACTION)
            .where(PROCESS_ACTION.ITEM_NO.eq(itemNo))
            .and(PROCESS_ACTION.DELETED.eq(DbConst.ALIVE))
            .fetchOneInto(ProcessAction.class);
    }


    public List<ProcessAction> batchGetActionByPipeId(Long pipeId) {
        return dslContext.selectFrom(PROCESS_ACTION)
            .where(PROCESS_ACTION.PIPE_ID.eq(pipeId))
            .and(PROCESS_ACTION.DELETED.eq(DbConst.ALIVE))
            .fetchInto(ProcessAction.class);
    }

    public Integer count(QueryActionReq req) {

        // 2. 使用 DSLContext 创建查询
        SelectQuery<Record> selectQuery = dslContext.selectQuery();

        // 3. 添加 COUNT 函数
        Field<Integer> countField = DSL.count().as("record_count"); // 定义一个字段用于存储记录总数
        selectQuery.addSelect(countField); // 添加 COUNT 函数到查询中
        selectQuery.addFrom(PROCESS_ACTION); // 指定要查询的表

        Field<Integer> field = DSL.field(PROCESS_ACTION.DELETED.getName(), Integer.class);
        Condition deleteCondition = field.eq(DbConst.ALIVE);
        selectQuery.addConditions(deleteCondition);

        if (null != req.getPipeId()) {
            Field<Long> pipeIdField = DSL.field(PROCESS_ACTION.PIPE_ID.getName(), Long.class);
            Condition pipeIdCondition = pipeIdField.eq(req.getPipeId());
            selectQuery.addConditions(pipeIdCondition);
        }
        if (null != req.getTaskId()) {
            Field<Long> taskIdField = DSL.field(PROCESS_ACTION.TASK_ID.getName(), Long.class);
            Condition taskIdCondition = taskIdField.eq(req.getTaskId());
            selectQuery.addConditions(taskIdCondition);
        }
        if (null != req.getProcessId()) {
            Field<Long> processIdField = DSL.field(PROCESS_ACTION.PROCESS_ID.getName(), Long.class);
            Condition processIdCondition = processIdField.eq(req.getProcessId());
            selectQuery.addConditions(processIdCondition);
        }
        if (StringUtils.isNotEmpty(req.getTaskCode())) {
            Field<String> taskCodeField = DSL.field(PROCESS_ACTION.TASK_CODE.getName(), String.class);
            Condition taskCodeCondition = taskCodeField.eq(req.getTaskCode());
            selectQuery.addConditions(taskCodeCondition);
        }
        if (StringUtils.isNotEmpty(req.getActionName())) {
            Field<String> nameField = DSL.field(PROCESS_ACTION.NAME.getName(), String.class);
            Condition nameCondition = nameField.eq(req.getActionName());
            selectQuery.addConditions(nameCondition);
        }
        if (CollectionUtils.isNotEmpty(req.getStatus())) {
            Field<String> statusField = DSL.field(PROCESS_ACTION.STATUS.getName(), String.class);
            Condition statusCondition = statusField.in(req.getStatus());
            selectQuery.addConditions(statusCondition);
        }
        if (StringUtils.isNotEmpty(req.getActionNo())) {
            Field<String> actionNoField = DSL.field(PROCESS_ACTION.ITEM_NO.getName(), String.class);
            Condition actionNoCondition = actionNoField.eq(req.getActionNo());
            selectQuery.addConditions(actionNoCondition);
        }
        if (StringUtils.isNotEmpty(req.getAdminAccount())) {
            Field<String> adminAccountField = DSL.field(PROCESS_ACTION.ADMIN_ACCOUNTS.getName(), String.class);
            Condition adminAccountCondition = adminAccountField.like("%" + req.getAdminAccount() + "%");
            selectQuery.addConditions(adminAccountCondition);
        }

        // 4. 执行查询
        Result<Record> result = selectQuery.fetch();
        // 获取记录总数
        return result.getValue(0, countField);
    }

    public List<ProcessAction> list(Integer pageNo, Integer pageSize, QueryActionReq req) {
        Integer offset = (pageNo - 1) * pageSize;

        SelectConditionStep<ProcessActionRecord> conditionStep =
            dslContext.selectFrom(PROCESS_ACTION).where(PROCESS_ACTION.DELETED.eq(DbConst.ALIVE));

        if (null != req.getPipeId()) {
            Field<Long> field = DSL.field(PROCESS_ACTION.PIPE_ID.getName(), Long.class);
            Condition pipeIdCondition = field.eq(req.getPipeId());
            conditionStep.and(pipeIdCondition);
        }
        if (null != req.getTaskId()) {
            Field<Long> field = DSL.field(PROCESS_ACTION.TASK_ID.getName(), Long.class);
            Condition taskIdCondition = field.eq(req.getTaskId());
            conditionStep.and(taskIdCondition);
        }
        if (null != req.getProcessId()) {
            Field<Long> field = DSL.field(PROCESS_ACTION.PROCESS_ID.getName(), Long.class);
            Condition processIdCondition = field.eq(req.getProcessId());
            conditionStep.and(processIdCondition);
        }
        if (StringUtils.isNotEmpty(req.getTaskCode())) {
            Field<String> taskCodeField = DSL.field(PROCESS_ACTION.TASK_CODE.getName(), String.class);
            Condition taskCodeCondition = taskCodeField.eq(req.getTaskCode());
            conditionStep.and(taskCodeCondition);
        }
        if (StringUtils.isNotEmpty(req.getActionName())) {
            Field<String> nameField = DSL.field(PROCESS_ACTION.NAME.getName(), String.class);
            Condition nameCondition = nameField.eq(req.getActionName());
            conditionStep.and(nameCondition);
        }
        if (CollectionUtils.isNotEmpty(req.getStatus())) {
            Field<String> statusField = DSL.field(PROCESS_ACTION.STATUS.getName(), String.class);
            Condition statusCondition = statusField.in(req.getStatus());
            conditionStep.and(statusCondition);
        }
        if (StringUtils.isNotEmpty(req.getActionNo())) {
            Field<String> actionNoField = DSL.field(PROCESS_ACTION.ITEM_NO.getName(), String.class);
            Condition actionNoCondition = actionNoField.eq(req.getActionNo());
            conditionStep.and(actionNoCondition);
        }
        if (StringUtils.isNotEmpty(req.getAdminAccount())) {
            Field<String> adminAccountField = DSL.field(PROCESS_ACTION.ADMIN_ACCOUNTS.getName(), String.class);
            Condition adminAccountCondition = adminAccountField.like("%" + req.getAdminAccount() + "%");
            conditionStep.and(adminAccountCondition);
        }

        return conditionStep.orderBy(PROCESS_ACTION.ID.desc())
            .limit(offset, pageSize)
            .fetchInto(ProcessAction.class);
    }

    public List<ProcessAction> list(Long taskId) {
        return dslContext.selectFrom(PROCESS_ACTION)
            .where(PROCESS_ACTION.TASK_ID.eq(taskId))
            .and(PROCESS_ACTION.DELETED.eq(DbConst.ALIVE))
            .fetchInto(ProcessAction.class);
    }

    public List<ProcessAction> listByStageNo(String stageNo) {
        return dslContext.selectFrom(PROCESS_ACTION)
            .where(PROCESS_ACTION.STAGE_NO.eq(stageNo))
            .and(PROCESS_ACTION.DELETED.eq(DbConst.ALIVE))
            .fetchInto(ProcessAction.class);
    }

    public List<ProcessAction> listByPipeId(Long pipeId) {
        return dslContext.selectFrom(PROCESS_ACTION)
            .where(PROCESS_ACTION.PIPE_ID.eq(pipeId))
            .and(PROCESS_ACTION.DELETED.eq(DbConst.ALIVE))
            .fetchInto(ProcessAction.class);
    }

    public List<ProcessAction> listByTaskInstId(Long taskInstId) {
        return dslContext.selectFrom(PROCESS_ACTION)
            .where(PROCESS_ACTION.TASK_ID.eq(taskInstId))
            .and(PROCESS_ACTION.DELETED.eq(DbConst.ALIVE))
            .fetchInto(ProcessAction.class);
    }

    public List<ProcessActionRecord> listByTask(Long processId, String taskCode) {
        return dslContext.selectFrom(PROCESS_ACTION)
            .where(PROCESS_ACTION.PROCESS_ID.eq(processId))
            .and(PROCESS_ACTION.TASK_CODE.eq(taskCode))
            .and(PROCESS_ACTION.DELETED.eq(DbConst.ALIVE)).fetch();
    }


    public void deleteActionByProcessInstId(Long processInstId) {
        dslContext.update(PROCESS_ACTION)
            .set(PROCESS_ACTION.DELETED, DbConst.DELETED)
            .where(PROCESS_ACTION.PROCESS_ID.eq(processInstId))
            .execute();
    }

    public int delete(String itemNo) {
        return dslContext.update(PROCESS_ACTION)
            .set(PROCESS_ACTION.DELETED, DbConst.DELETED)
            .where(PROCESS_ACTION.ITEM_NO.eq(itemNo))
            .execute();
    }

    public void updateItemStatus(String itemNo, String oldStatus, String newStatus) {
        dslContext.update(PROCESS_ACTION)
            .set(PROCESS_ACTION.STATUS, newStatus)
            .where(PROCESS_ACTION.ITEM_NO.eq(itemNo))
            .and(PROCESS_ACTION.STATUS.eq(oldStatus))
            .execute();
    }


    private ProcessActionRecord buildRecord(String itemNo, JsonNode param, JsonNode result, String errMsg,
                                            ExecModeEnum execMode, ActionStatusEnum status, String ucid,
                                            String name, String clazzName, String context, String inputScript) {
        BizAssertUtils.notEmpty(itemNo, "itemNo can not be null");
        ProcessActionRecord record = new ProcessActionRecord();
        record.setItemNo(itemNo);
        if (null != param) {
            record.setParam(JsonUtils.obj2Str(param));
        }
        if (null != result) {
            record.setResult(JsonUtils.obj2Str(result));
        }
        if (StringUtils.isNotEmpty(errMsg)) {
            record.setErrMsg(errMsg);
        }
        if (null != execMode) {
            record.setExecMode(execMode.getCode());
        }
        if (null != status) {
            record.setStatus(status.status);
        }
        if (StringUtils.isNotEmpty(ucid)) {
            record.setUcid(ucid);
        }
        if (StringUtils.isNotBlank(name)) {
            record.setName(name);
        }
        if (StringUtils.isNotBlank(clazzName)) {
            record.setClazz(clazzName);
        }
        if (StringUtils.isNotBlank(context)) {
            record.setContext(context);
        }
        if (StringUtils.isNotBlank(inputScript)) {
            record.setInputScript(inputScript);
        }
        if (record.getDoneTime() == null) {
            record.setDoneTime(DateUtil.DEFAULT_START_TIME);
        }
        if (record.getCtime() == null) {
            record.setCtime(DateUtil.DEFAULT_START_TIME);
        }
        if (record.getMtime() == null) {
            record.setMtime(DateUtil.DEFAULT_START_TIME);
        }
        return record;
    }

    private void applyRequiredDefaults(ProcessActionRecord record, AbsAction<?, ?> absAction) {
        if (record.getItemNo() == null) {
            record.setItemNo(StringUtils.defaultString(absAction.getItemNo()));
        }
        if (record.getStageNo() == null) {
            record.setStageNo(StringUtils.defaultString(absAction.getStageNo()));
        }
        if (record.getName() == null) {
            record.setName(StringUtils.defaultString(absAction.getName()));
        }
        if (record.getClazz() == null) {
            record.setClazz(StringUtils.defaultString(absAction.getClazz()));
        }
        if (record.getTaskId() == null) {
            record.setTaskId(absAction.getTaskId() == null ? 0L : absAction.getTaskId());
        }
        if (record.getTaskCode() == null) {
            record.setTaskCode(StringUtils.defaultString(absAction.getTaskCode()));
        }
        if (record.getProcessId() == null) {
            record.setProcessId(absAction.getProcessId() == null ? 0L : absAction.getProcessId());
        }
        if (record.getStatus() == null && absAction.getStatus() != null) {
            record.setStatus(absAction.getStatus().status);
        }
        if (record.getExecMode() == null && absAction.getExecMode() != null) {
            record.setExecMode(absAction.getExecMode().getCode());
        }
        if (record.getUcid() == null) {
            record.setUcid(StringUtils.defaultString(absAction.getUcid()));
        }
        if (record.getAdminAccounts() == null) {
            record.setAdminAccounts(StringUtils.defaultString(absAction.getAdminAccounts()));
        }
        if (record.getDoneTime() == null) {
            record.setDoneTime(DateUtil.DEFAULT_START_TIME);
        }
        if (record.getCtime() == null) {
            record.setCtime(DateUtil.DEFAULT_START_TIME);
        }
        if (record.getMtime() == null) {
            record.setMtime(DateUtil.DEFAULT_START_TIME);
        }
        if (record.getDeleted() == null) {
            record.setDeleted(DbConst.ALIVE);
        }
    }

}
