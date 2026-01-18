package com.merchant.pipe.stage;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.merchant.kernel.pipe.pipe.PipeContext;
import com.merchant.kernel.pipe.pipe.Result;

/**
 * Create on 2023/5/1
 */
public interface IStageLifeCycle {

    void init(Long pipeId, String nextStageNo, StageDef stageDef, PipeContext pipeContext)
        throws ClassNotFoundException;

    Result startup();


    /**
     * 强制执行当前stage中的action
     * 强制完成，不去考虑是否有场景支持
     *
     * @param force
     * @return
     */
    Result tryCompleteAllAction(Boolean force);


    void shutdown();

    void signal(String actionNo, ObjectNode actionOutput);

    void triggerNextStageActionList(String actionNo);

}
