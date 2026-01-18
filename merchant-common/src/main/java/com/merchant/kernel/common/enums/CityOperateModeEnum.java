package com.merchant.kernel.common.enums;

public enum CityOperateModeEnum {
    UNKNOWN(0),
    DIRECT(1),
    FRANCHISE(2);

    private final int value;

    CityOperateModeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
