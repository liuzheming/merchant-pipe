package com.merchant.kernel.pipe;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 等待  code IN [100, 200)
 * 完成  code IN [200, 300)
 * 失败  code IN [500, 600)
 * <p>
 * Create on 2022/2/18
 */
@Getter
public enum ActionStatusEnum {

    CREATED("CREATED", "待执行"),

    INIT_SUCC("INIT_SUCC", "初始化完成"),

    WAIT_INIT("INIT_SUCC", "等待初始化"),



    EXEC_FAIL("EXEC_FAIL", "已失败"),

    EXEC_SUCC("EXEC_SUCC", "已完成"),

    EXEC_SKIP("EXEC_SKIP", "已跳过"),
    // 此类状态提供给自定义场景中
    WAIT_EXEC("WAIT_EXEC", "等待完成");

    ActionStatusEnum(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static ActionStatusEnum of(String status) {
        if (StringUtils.isEmpty(status)) {
            return null;
        }

        for (ActionStatusEnum statusEnum : ActionStatusEnum.values()) {
            if (statusEnum.status.equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }


    public final String status;

    public final String desc;

}
