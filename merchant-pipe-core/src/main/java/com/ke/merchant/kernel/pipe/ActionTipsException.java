package com.ke.merchant.kernel.pipe;

import com.ke.merchant.kernel.common.exception.ServiceException;

/**
 * Create on 2024/6/3
 * by KaerKing
 * 为了提示任务执行记录使用
 */

public class ActionTipsException extends ServiceException {

    public ActionTipsException(String msg) {
        super(msg);
    }

    public ActionTipsException(Throwable e) {
        super(e);
    }

    public ActionTipsException(String msg, Throwable e) {
        super(msg, e);
    }

}
