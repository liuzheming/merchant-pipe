package com.merchant.trigger;

import com.merchant.common.exception.ServiceException;

/**
 * Create on 2022/4/4
 */
public class TriggerException extends ServiceException {

    public TriggerException(String msg) {
        super(msg);
    }

    public TriggerException(Throwable e) {
        super(e);
    }

    public TriggerException(String msg, Throwable e) {
        super(msg, e);
    }
}
