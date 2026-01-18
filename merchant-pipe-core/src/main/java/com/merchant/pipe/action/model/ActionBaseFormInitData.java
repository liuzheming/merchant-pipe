package com.merchant.pipe.action.model;

import com.merchant.kernel.pipe.action.AbsAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionBaseFormInitData {
    private String actionItemNo;

    private String actionName;
    private String actionDesc;
    private String actionDescAttach;
    /**
     * 若action存在跳链
     */
    private List<AbsAction.ActionButton> buttons;
    /**
     * action表单额外参数
     */
    private Map<String, String> additionalData;

}
