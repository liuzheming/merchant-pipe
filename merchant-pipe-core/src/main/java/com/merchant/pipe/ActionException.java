package com.merchant.pipe;

import com.merchant.kernel.common.exception.ServiceErrorException;

/**
 * Create on 2022/2/18
 */
public class ActionException extends ServiceErrorException {

    public ActionException(String msg) {
        super(msg);
    }

    public ActionException(Throwable e) {
        super(e);
    }

    public ActionException(String msg, Throwable e) {
        super(msg, e);
    }

}
