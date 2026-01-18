package com.ke.merchant.kernel.pipe.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Create on 2022/12/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionContext {

    /**
     * 主流程实例ID
     */
    private Long mainProcInstId;
    private Long taskId;
    private Long processId;
    private String procCode;
    private String taskCode;
    private String ucid;
    private String brandMasterTaxNo;
    private String franchiserId;
    private String franchiserNo;
    private String franchiserTaxNo;
    private String storeId;
    private String linkStoreNo;
    private String cityCode;

    private String cityName;
    /**
     * 跳过逻辑：如果为true那么则可以直接跳过
     */
    private boolean skipFlag;
    private LocalDateTime effectiveTime;


    /**
     * 区代相关信息
     * 合作模式 INNER_DIRECT  AREA_PROXY , AREA_PROXY 时为区代
     */
    private String cityOperateMode;
    /**
     * 区代相关信息
     * 城市平台公司 mdmCode
     */
    private String cityPlatComMdmCode;

    @Builder.Default
    private Map<String, String> extData = new LinkedHashMap<>();
    @Builder.Default
    private ObjectNode input = new ObjectMapper().createObjectNode();
    @Builder.Default
    private ObjectNode output = new ObjectMapper().createObjectNode();
    @Builder.Default
    private RetryParam retryParam = new RetryParam();

    @Data
    public static class RetryParam {

        /**
         * 最大重试次数, 默认 1 次，也就是不重试
         */
        private Integer maxAttempts = 1;
        /**
         * 第一次重试的等待时间，默认 1.5s
         */
        private Long initialIntervalMs = 1500L;
        /**
         * 最大的重试等待时间，默认 10s
         */
        private Long maxIntervalMs = 10000L;
        /**
         * 重试失败的退避时间，默认 2 倍
         */
        private Double multiplier = 1.0;
    }

}
