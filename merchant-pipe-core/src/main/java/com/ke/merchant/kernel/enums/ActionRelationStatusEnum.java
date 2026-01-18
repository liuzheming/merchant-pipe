package com.merchant.enums;

public enum ActionRelationStatusEnum {
    INIT(0, "init"),
    FINISH(1, "finish");

    private final int code;
    private final String desc;

    ActionRelationStatusEnum(int code, String desc) {
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
