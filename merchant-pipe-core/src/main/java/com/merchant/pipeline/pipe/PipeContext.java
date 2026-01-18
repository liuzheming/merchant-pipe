package com.merchant.pipeline.pipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.merchant.kernel.enums.ExecModeEnum;
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
public class PipeContext {

    private String ownerId;
    private String ownerType;
    private String ownerName;
    private String ownerComName;
    private LocalDateTime ownerCreateDate;

    private Long taskId;
    private Long processId;
    /**
     * 主流程实例ID
     */
    private Long mainProcInstId;

    /**
     * pipe类型
     */
    private PipeTypeEnum pipeType;
    private String procCode;
    private String taskCode;
    private String ucid;
    private String brandMasterTaxNo;
    private String brandMasterNo;
    private String franchiserId;
    private String franchiserNo;
    private String franchiserTaxNo;
    private String storeComTaxNo;
    private String storeId;
    private String linkStoreNo;
    private String cityCode;
    private String cityName;
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
    /**
     * 跳过逻辑：如果为true那么则可以直接跳过
     */
    private boolean skipFlag;
    private LocalDateTime effectiveTime;
    @Builder.Default
    private Map<String, String> extData = new LinkedHashMap<>();
    @Builder.Default
    private ObjectNode pipeData = new ObjectMapper().createObjectNode();
    private ExecModeEnum forceExecMode;

    public PipeContext(PipeContext original) {
        this.ownerId = original.ownerId;
        this.ownerType = original.ownerType;
        this.ownerName = original.ownerName;
        this.ownerComName = original.ownerComName;
        this.ownerCreateDate = original.ownerCreateDate;
        this.taskId = original.taskId;
        this.processId = original.processId;
        this.procCode = original.procCode;
        this.taskCode = original.taskCode;
        this.ucid = original.ucid;
        this.brandMasterTaxNo = original.brandMasterTaxNo;
        this.brandMasterNo = original.brandMasterNo;
        this.franchiserId = original.franchiserId;
        this.franchiserNo = original.franchiserNo;
        this.franchiserTaxNo = original.franchiserTaxNo;
        this.storeComTaxNo = original.storeComTaxNo;
        this.storeId = original.storeId;
        this.linkStoreNo = original.linkStoreNo;
        this.mainProcInstId = original.mainProcInstId;
        this.pipeType = original.pipeType;
        this.cityCode = original.cityCode;
        this.cityName = original.cityName;
        this.skipFlag = original.skipFlag;
        this.effectiveTime = original.effectiveTime;
        this.extData = new LinkedHashMap<>(original.extData);
        this.pipeData = original.pipeData.deepCopy(); // Assuming deepCopy() correctly clones the ObjectNode
        this.cityOperateMode = original.cityOperateMode;
        this.forceExecMode = original.forceExecMode;
    }

}
