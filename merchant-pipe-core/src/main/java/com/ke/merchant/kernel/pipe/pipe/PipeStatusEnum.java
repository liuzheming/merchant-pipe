package com.ke.merchant.kernel.pipe.pipe;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 等待  code IN [100, 200)
 * 完成  code IN [200, 300)
 * 失败  code IN [500, 600)
 *
 * Create on 2022/2/18
 */
@Getter
public enum PipeStatusEnum {

    CREATED("CREATED", "已创建"),

    PENDING("PENDING", "待运行"),

    RUNNING("RUNNING", "运行中"),

    DELETED("DELETED", "删除"),

    DONE("DONE", "已完成");

    PipeStatusEnum(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static PipeStatusEnum of(String status) {
        if (StringUtils.isEmpty(status)) {
            return null;
        }

        for (PipeStatusEnum statusEnum : PipeStatusEnum.values()) {
            if (statusEnum.status.equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }


    public final String status;

    public final String desc;

}
