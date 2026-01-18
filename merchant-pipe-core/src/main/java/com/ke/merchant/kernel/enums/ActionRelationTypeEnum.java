package com.ke.merchant.kernel.enums;

public enum ActionRelationTypeEnum {
    FORM_RELATION(1, "form"),
    FORM_RELATION_ONLY_SHOW(2, "form_show");

    private final int code;
    private final String desc;

    ActionRelationTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
