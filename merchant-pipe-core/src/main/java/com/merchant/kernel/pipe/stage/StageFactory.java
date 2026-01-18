package com.merchant.kernel.pipe.stage;

import com.merchant.kernel.pipe.pipe.PipeContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Create on 2023/5/1
 */
@Component
public class StageFactory implements IStageFactory {

    @Resource
    private StageRepo stageRepo;
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public Stage create(Long pipeId, String stageNo, String nextStageNo, StageDef stageDef, PipeContext pipeContext)
        throws ClassNotFoundException {
        Stage stage = new Stage();
        stage.setPipeId(pipeId);
        stage.setStageNo(stageNo);
        stage.setNextStageNo(nextStageNo);
        Long id = stageRepo.create(stage);
        stage.setId(id);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(stage);
        stage.init(pipeId, nextStageNo, stageDef, pipeContext);
        return stage;
    }

    @Override
    public Stage get(String stageNo) {
        Stage stage = stageRepo.get(stageNo);
        if (null == stage) {
            return null;
        }
        applicationContext.getAutowireCapableBeanFactory().autowireBean(stage);
        return stage;
    }
}
