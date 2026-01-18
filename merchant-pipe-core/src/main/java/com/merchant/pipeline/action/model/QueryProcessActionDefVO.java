package com.merchant.pipeline.action.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangmeng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryProcessActionDefVO {
    private Integer actionSeqNum;
    private String actionNo;
    private String actionName;
    private Integer actionType;
    private String actionTypeDesc;
    private String actionRange;
    private String actionRangeExclude;
    private String actionRangeDesc;
    private String actionRangeExcludeDesc;
    private Integer actionRangeInvertFlag;
    private Integer actionGroup;
    private String actionGroupDesc;
    private String actionProcess;
    private String actionProcessDesc;
    private Integer actionScene;
    private String actionSceneDesc;
    private String actionDesc;
    private String actionDescAttach;
    private List<FcnFile> actionDescAttachFile;
    private Integer actionCompleteType;
    private String actionDependency;
    private String actionDependencyDesc;
    private Integer needNotice;
    private Integer actionStatus;
    private String actionStatusDesc;
    private Integer actionHasFile;
    private String actionCreatorName;
    private String actionUpdaterName;
    private String actionCreateTime;
    private String actionUpdateTime;
    private String actionBrandName;
    private String actionBrandNameExclude;
    private String actionBrandNo;
    private String actionBrandNoExclude;
    private Integer actionBrandInvertFlag;
    private String bizType;
    private Integer actionStatistic;

    @Data
    public static class FcnFile {
        private String name;
        private String url;
        private String value;
    }
}
