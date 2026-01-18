package com.ke.merchant.kernel.pipe;

import com.ke.merchant.kernel.common.exception.ServiceException;

/**
 * Description:
 * <p>
 *
 * @author lzm
 * @date 2023/7/5
 */
public class ActionSkipException extends ServiceException {

    private String skipDesc;

    public ActionSkipException() {

    }

    public ActionSkipException(String msg) {
        this.skipDesc = msg;
    }

    public ActionSkipException(Throwable e) {
        super(e);
    }

    public ActionSkipException(String msg, Throwable e) {
        super(msg, e);
    }


}
