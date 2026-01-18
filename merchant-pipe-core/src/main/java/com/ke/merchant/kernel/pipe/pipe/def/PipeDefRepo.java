package com.merchant.pipe.pipe.def;

import com.merchant.common.constant.DbConst;
import com.merchant.pipe.db.Tables;
import com.merchant.pipe.db.tables.pojos.ProcessActionPipeDef;
import com.merchant.pipe.db.tables.records.ProcessActionPipeDefRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.merchant.pipe.db.Tables.PROCESS_ACTION_PIPE_DEF;

/**
 * Create on 2023/4/30
 */
@Component
public class PipeDefRepo {

    @Resource
    private DSLContext dslContext;

    public int insert(ProcessActionPipeDef pipeDefEntity) {
        ProcessActionPipeDefRecord pipeDefRecord =
            com.merchant.common.utils.BeanCopyUtils.build(pipeDefEntity, ProcessActionPipeDefRecord.class, true);
        return dslContext.insertInto(Tables.PROCESS_ACTION_PIPE_DEF)
            .set(pipeDefRecord)
            .execute();
    }

    public int update(ProcessActionPipeDef pipeDefEntity) {
        ProcessActionPipeDefRecord pipeDefRecord =
            com.merchant.common.utils.BeanCopyUtils.build(pipeDefEntity, ProcessActionPipeDefRecord.class, true);
        return dslContext.update(Tables.PROCESS_ACTION_PIPE_DEF)
            .set(pipeDefRecord)
            .where(Tables.PROCESS_ACTION_PIPE_DEF.ID.eq(pipeDefEntity.getId()))
            .execute();
    }


    public int delete(Long pipeDefId) {
        return dslContext.update(Tables.PROCESS_ACTION_PIPE_DEF)
            .set(Tables.PROCESS_ACTION_PIPE_DEF.DELETED, 1)
            .where(Tables.PROCESS_ACTION_PIPE_DEF.ID.eq(pipeDefId))
            .execute();
    }

    public Integer count(String pipeDefName) {

        // 2. 使用 DSLContext 创建查询
        SelectQuery<Record> selectQuery = dslContext.selectQuery();

        // 3. 添加 COUNT 函数
        Field<Integer> countField = DSL.count().as("record_count"); // 定义一个字段用于存储记录总数
        selectQuery.addSelect(countField); // 添加 COUNT 函数到查询中
        selectQuery.addFrom(PROCESS_ACTION_PIPE_DEF); // 指定要查询的表

        Field<Integer> field = DSL.field(PROCESS_ACTION_PIPE_DEF.DELETED.getName(), Integer.class);
        Condition deleteCondition = field.eq(DbConst.ALIVE);
        selectQuery.addConditions(deleteCondition);

        if (StringUtils.isNotEmpty(pipeDefName)) {
            Field<String> pipeNameField = DSL.field(PROCESS_ACTION_PIPE_DEF.NAME.getName(), String.class);
            Condition pipeNameCondition = pipeNameField.like("%" + pipeDefName + "%");
            selectQuery.addConditions(pipeNameCondition);
        }

        // 4. 执行查询
        Result<Record> result = selectQuery.fetch();
        // 获取记录总数
        return result.getValue(0, countField);
    }

    public List<ProcessActionPipeDef> list(String pipeDefName, Integer pageNo, Integer pageSize) {
        SelectConditionStep<ProcessActionPipeDefRecord> step =
            dslContext.selectFrom(PROCESS_ACTION_PIPE_DEF)
                .where(PROCESS_ACTION_PIPE_DEF.DELETED.eq(0));

        if (StringUtils.isNotEmpty(pipeDefName)) {
            Field<String> pipeName = DSL.field(PROCESS_ACTION_PIPE_DEF.NAME.getName(), String.class);
            Condition pipeNameCondition = pipeName.like("%" + pipeDefName + "%");
            step.and(pipeNameCondition);
        }
        step.limit(((pageNo - 1) * pageSize), (int) pageSize);
        return step.fetchInto(ProcessActionPipeDef.class);
    }

    public ProcessActionPipeDef getByCode(String pipeDefCode) {
        return dslContext.selectFrom(Tables.PROCESS_ACTION_PIPE_DEF)
            .where(Tables.PROCESS_ACTION_PIPE_DEF.DELETED.eq(0))
            .and(Tables.PROCESS_ACTION_PIPE_DEF.CODE.eq(pipeDefCode))
            .fetchOneInto(ProcessActionPipeDef.class);
    }

    public ProcessActionPipeDef getByTask(String procCode, String taskCode) {
        return dslContext.selectFrom(Tables.PROCESS_ACTION_PIPE_DEF)
            .where(Tables.PROCESS_ACTION_PIPE_DEF.DELETED.eq(0))
            .and(Tables.PROCESS_ACTION_PIPE_DEF.PROC_CODE.eq(procCode))
            .and(Tables.PROCESS_ACTION_PIPE_DEF.TASK_CODE.eq(taskCode))
            .fetchOneInto(ProcessActionPipeDef.class);
    }

}
