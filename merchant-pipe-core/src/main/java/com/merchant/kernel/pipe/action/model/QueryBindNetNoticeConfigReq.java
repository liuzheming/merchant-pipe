package com.merchant.kernel.pipe.action.model;

import lombok.Data;

/**
 * @author zhangmeng
 * @create 2025-02-08 4:00 下午
 */
@Data
public class QueryBindNetNoticeConfigReq {
    private String smsTemPlate;
    private Integer pageNo;
    private Integer pageSize = 10;
}
