package com.merchant.pipe.facade.model;

import lombok.Data;

import java.util.List;

/**
 * Create on 2023/4/30
 */
@Data
public class CreatePipeReq {

    private String name;
    private String code;
    private String procCode;
    private String taskCode;
    private List<String> stageDefList;
    private String stageDef;

}
