package com.merchant.pipe.action;

import com.merchant.enums.ExecModeEnum;
import com.merchant.pipe.ActionStatusEnum;

import java.util.List;

/**
 * Description:
 * <p>
 * Created by lzm on 2022/2/24.
 */
public interface IAction {

    ActionResult<?> skip();

    /**
     * 获取 action 的执行方式，用于自动触发场景，ActionList 执行 action 之前，会先判断 action
     * 的 mode，然后根据 mode 选择对应的执行方式
     *
     * @return ExecModeEnum
     */
    ExecModeEnum getExecMode();

    /**
     * 查询 action 类型
     */
    String getType();


    Long getProcessId();

    Long getTaskId();

    Long getId();

    ActionStatusEnum getStatus();

    String getErrMsg();

    String getItemNo();

    String getStageNo();

    Long getPipeId();

    String getName();

    Boolean done();

    ActionContext getContext();

    List<AbsAction.ActionButton> getButtonList();

    public boolean checkSkip();


}
