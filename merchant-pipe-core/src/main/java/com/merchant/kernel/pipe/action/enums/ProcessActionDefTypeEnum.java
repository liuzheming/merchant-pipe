package com.merchant.kernel.pipe.action.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhangmeng
 * @create 2024-06-06 11:50 上午
 */
@AllArgsConstructor
@Getter
public enum ProcessActionDefTypeEnum {
    PLATFORM(0, "平台任务"),
    CITY_CUSTOM(1, "城市自定义任务");

    private final Integer code;
    private final String desc;


    public static ProcessActionDefTypeEnum codeOf(Integer code) {
        for (ProcessActionDefTypeEnum processActionDefTypeEnum : ProcessActionDefTypeEnum.values()) {
            if (processActionDefTypeEnum.getCode().equals(code)) {
                return processActionDefTypeEnum;
            }
        }
        throw new IllegalArgumentException("no ProcessActionDefTypeEnum for code" + code);
    }
}
