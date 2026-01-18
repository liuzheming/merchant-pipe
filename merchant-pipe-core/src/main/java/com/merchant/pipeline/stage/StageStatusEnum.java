package com.merchant.pipeline.stage;

import org.apache.commons.lang3.StringUtils;

/**
 * 等待  code IN [100, 200)
 * 完成  code IN [200, 300)
 * 失败  code IN [500, 600)
 * <p>
 * Create on 2022/2/18
 */
public enum StageStatusEnum {

    CREATED("CREATED", "已创建"),

    PENDING("PENDING", "待运行"),

    RUNNING("RUNNING", "运行中"),

    DONE("DONE", "已完成");

    StageStatusEnum(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static StageStatusEnum of(String status) {
        if (StringUtils.isEmpty(status)) {
            return null;
        }

        for (StageStatusEnum statusEnum : StageStatusEnum.values()) {
            if (statusEnum.status.equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }


    public final String status;

    public final String desc;

}
