package com.ke.merchant.kernel.pipe.facade.model;

import lombok.Data;

import java.util.List;

/**
 * Create on 2023/4/30
 */
@Data
public class EditPipeReq {

    private Long id;
    private String name;
    private String code;
    private String procCode;
    private String taskCode;
    private String stageDef;
    private List<AdvancedTransformer.OriginGroup> actionDefs;
}
