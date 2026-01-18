package com.merchant.kernel.pipe.action.model;

import lombok.Data;

/**
 * @author zhangmeng
 * @create 2024-06-06 11:19 上午
 */
@Data
public class QueryProcessActionDefReq {
    private Long actionNo;
    private String actionName;
    private Integer actionType;
    private String actionRange;
    /**
     * 任务反选范围
     */
    private String actionRangeExclude;
    private Integer actionGroup;
    private String actionProcess;
    private Integer actionStatus;
    private Integer pageNo;
    private Integer pageSize = 10;
}
