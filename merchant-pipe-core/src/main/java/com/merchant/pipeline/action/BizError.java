package com.merchant.pipeline.action;

import lombok.Data;

/**
 * Description:
 * <p>
 *
 * @author lzm
 * @date 2023/7/14
 */
@Data
public class BizError {

    private String errmsg;
    /**
     * 跟进人账号，用逗号隔开
     */
    private String followUpAccounts;

    public BizError(String errmsg) {
        this.errmsg = errmsg;
    }
}
