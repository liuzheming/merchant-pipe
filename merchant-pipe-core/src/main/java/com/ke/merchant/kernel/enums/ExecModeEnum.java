package com.ke.merchant.kernel.enums;

public enum ExecModeEnum {
    AUTO("AUTO", "auto"),
    MANUAL("MANUAL", "manual"),
    SKIP("SKIP", "skip");

    private final String code;
    private final String desc;

    ExecModeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ExecModeEnum of(String code) {
        for (ExecModeEnum modeEnum : values()) {
            if (modeEnum.code.equals(code)) {
                return modeEnum;
            }
        }
        return MANUAL;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
