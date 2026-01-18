package com.merchant.pipeline;

import com.merchant.kernel.common.exception.ServiceException;

/**
 * Create on 2022/2/18
 */
public class ActionStuckException extends ServiceException {

    public ActionStuckException(String msg) {
        super(msg);
    }

    public ActionStuckException(Throwable e) {
        super(e);
    }

    public ActionStuckException(String msg, Throwable e) {
        super(msg, e);
    }

}
