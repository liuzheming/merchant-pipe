package com.merchant.kernel.pipe.action.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhangmeng
 * @create 2024-06-04 9:27 下午
 */
@Getter
@AllArgsConstructor
public enum ProcessActionDefSceneEnum {
    ALL_ENTER(0, "通用"),
    NORMAL_ENTER(1, "常规入驻"),
    EXTERNAL_CHANGE_INTERNAL_ENTER(2, "外转内入驻");


    private final Integer code;
    private final String desc;


    public static ProcessActionDefSceneEnum codeOf(Integer code) {
        for (ProcessActionDefSceneEnum processActionDefSceneEnum : ProcessActionDefSceneEnum.values()) {
            if (processActionDefSceneEnum.getCode().equals(code)) {
                return processActionDefSceneEnum;
            }
        }
        throw new IllegalArgumentException("no ProcessActionDefSceneEnum for code" + code);
    }
}
