package com.merchant.pipeline.action.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author KaerKing
 * @create 2024-06-17 11:50 上午
 */
@AllArgsConstructor
@Getter
public enum ProcessActionCompleteTypeEnum {
    AUTO(0, "自动完成"),
    MANUAL(1, "手动完成");

    private final Integer code;
    private final String desc;


    public static ProcessActionCompleteTypeEnum codeOf(Integer code) {
        for (ProcessActionCompleteTypeEnum processActionDefTypeEnum : ProcessActionCompleteTypeEnum.values()) {
            if (processActionDefTypeEnum.getCode().equals(code)) {
                return processActionDefTypeEnum;
            }
        }
        throw new IllegalArgumentException("no ProcessActionDefTypeEnum for code" + code);
    }
}
