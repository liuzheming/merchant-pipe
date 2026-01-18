package com.merchant.pipeline.action.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhangmeng
 * @create 2024-06-06 11:55 上午
 */
@Getter
@AllArgsConstructor
public enum ProcessActionDefGroupEnum {
    BRAND_BIND_NET(0, "品牌并网任务"),
    STORE_BIND_NET(1, "门店并网任务");

    private final Integer code;
    private final String desc;


    public static ProcessActionDefGroupEnum codeOf(Integer code) {
        for (ProcessActionDefGroupEnum processActionDefGroupEnum : ProcessActionDefGroupEnum.values()) {
            if (processActionDefGroupEnum.getCode().equals(code)) {
                return processActionDefGroupEnum;
            }
        }
        throw new IllegalArgumentException("no ProcessActionDefGroupEnum for code" + code);
    }
    // 获取所有枚举
    public static ProcessActionDefGroupEnum[] getAll() {
        return ProcessActionDefGroupEnum.values();
    }
}
