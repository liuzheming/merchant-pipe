package com.merchant.pipeline.action.model;

import lombok.Data;

/**
 * @author zhangmeng
 * @create 2025-02-08 3:55 下午
 */
@Data
public class QueryBindNetNoticeConfigVO {
    private Integer id;
    private String templateKey;
    private String templateName;
    private String city;

}
