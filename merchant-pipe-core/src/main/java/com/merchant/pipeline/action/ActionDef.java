package com.merchant.pipeline.action;

import com.merchant.kernel.enums.ExecModeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Create on 2022/2/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionDef {

    public ActionDef(String name, String clazz, Long formDefId, ExecModeEnum execMode) {
        this.name = name;
        this.clazz = clazz;
        this.formDefId = formDefId;
        this.execMode = execMode;
    }

    private String name;
    private String clazz;
    private String tip;

    private Long formDefId;
    /**
     * trigger mode
     */
    private ExecModeEnum execMode;

    private Integer maxAttempts;
    private Long initialInterval;
    private Long maxInterval;
    private Double multiplier;
    /**
     * action 的管理员 account，逗号隔开
     */
    private String adminAccounts;
    private String inputScript;

    private Map<String, String> extData = new HashMap<>();

}
