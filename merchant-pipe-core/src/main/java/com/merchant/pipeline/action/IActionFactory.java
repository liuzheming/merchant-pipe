package com.merchant.pipeline.action;

import java.util.List;

public interface IActionFactory {

    IAction createAction(Long pipeId, String stageNo, ActionDef actionDef, ActionContext context)
        throws ClassNotFoundException;

    IAction createAction4CustomTask(Long pipeId, String stageNo, ActionDef actionDef, ActionContext context)
        throws ClassNotFoundException;

    IAction loadAction(String itemNo);

    List<IAction> loadActionByStageNo(String stageNo);

}
