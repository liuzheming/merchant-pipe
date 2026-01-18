package com.merchant.pipe.action.model;

import com.merchant.pipe.action.model.QueryProcessActionDefVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangmeng
 * @create 2024-06-06 4:05 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProcessActionDefParam {
    private Long actionNo;
    private String actionName;
    private Integer actionType;
    /**
     * 任务范围
     */
    private String actionRange;
    /**
     * 任务反选范围
     */
    private String actionRangeExclude;
    private Integer actionGroup;
    private String actionProcess;
    private String actionDesc;
    private List<QueryProcessActionDefVO.FcnFile> actionDescAttachFile;
    private Integer actionCompleteType;
    private String actionDependency;
    /**
     * 0 内渠，1 区代
     */
    private String bizType;
    /**
     * 品牌反选范围
     */
    private String actionBrandNoExclude;
    /**
     * action对应的brandNo
     * 多个brandNo用逗号分隔
     */
    private String actionBrandNo;

    /**
     * action对应的brandName
     * 多个brandName用逗号分隔
     */
    private String actionBrandName;
    /**
     * 并网管理看板是否统计
     * 1表示是，0表示否
     */
    private Integer actionStatistic;
    /**
     * 任务范围是否反选；1-是，0-否
     */
    private Integer actionRangeInvertFlag;
    /**
     * 品牌范围是否反选；1-是，0-否
     */
    private Integer actionBrandInvertFlag;
}
