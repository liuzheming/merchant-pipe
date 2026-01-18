package com.merchant.kernel.pipe.stage;

import com.merchant.kernel.common.utils.BeanCopyUtils;
import com.merchant.kernel.common.utils.CheckUtil;
import com.merchant.kernel.pipe.db.tables.pojos.ProcessActionStage;
import com.merchant.kernel.pipe.db.tables.records.ProcessActionStageRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.merchant.kernel.pipe.db.Tables.PROCESS_ACTION_STAGE;


/**
 * Create on 2022/2/18
 */
@Slf4j
@Component
public class StageRepo {

    @Resource
    private DSLContext dslContext;

    public List<Stage> list(Long pipeId) {
        List<ProcessActionStageRecord> records
            = dslContext.select().from(PROCESS_ACTION_STAGE)
            .where(PROCESS_ACTION_STAGE.PIPE_ID.eq(pipeId))
            .fetchInto(ProcessActionStageRecord.class);
        List<Stage> list = new ArrayList<>();
        for (ProcessActionStageRecord record : records) {
            Stage stage = BeanCopyUtils.build(record, Stage.class, true);
            list.add(stage);
        }
        return list;
    }

    public Stage get(String stageNo) {
        ProcessActionStage recordOptional
            = dslContext.select().from(PROCESS_ACTION_STAGE)
            .where(PROCESS_ACTION_STAGE.STAGE_NO.eq(stageNo))
            .fetchOneInto(ProcessActionStage.class);
        if (null == recordOptional) {
            return null;
        }
        return BeanCopyUtils.build(recordOptional, Stage.class, true);
    }

    public Long create(Stage stage) {

        ProcessActionStageRecord record = BeanCopyUtils.build(stage, ProcessActionStageRecord.class, true);
        record.setStatus(StageStatusEnum.CREATED.status);
        record.setCtime(LocalDateTime.now());
        record.setMtime(LocalDateTime.now());
        ProcessActionStageRecord recordWithId = dslContext.insertInto(PROCESS_ACTION_STAGE)
            .set(record).returning(PROCESS_ACTION_STAGE.ID).fetchOne();
        return recordWithId.getId();

    }

    public int update(Stage stage) {
        ProcessActionStageRecord record = BeanCopyUtils.build(stage, ProcessActionStageRecord.class);
        int i = dslContext.update(PROCESS_ACTION_STAGE)
            .set(record).where(PROCESS_ACTION_STAGE.ID.eq(stage.getId())).execute();
        CheckUtil.isTrue(i == 1, "update actionGroup fail");
        return i;
    }

}
