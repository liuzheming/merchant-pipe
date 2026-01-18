package com.merchant.pipe.stage;

import com.merchant.pipe.pipe.PipeContext;

/**
 * Create on 2023/5/1
 */
public interface IStageFactory {

    Stage create(Long pipeId, String stageNo, String nextStageNo, StageDef stageDef, PipeContext pipeContext)
        throws ClassNotFoundException;

    Stage get(String stageNo);

}
