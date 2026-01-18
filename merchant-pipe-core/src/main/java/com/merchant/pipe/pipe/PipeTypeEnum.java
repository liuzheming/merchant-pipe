package com.merchant.pipe.pipe;


/**
 * pipe类型
 */
public enum PipeTypeEnum {

    // 默认普通类型
    NORMAL(0, "普通类型"),

    BIND_NET(1, "并网类型");

    PipeTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static PipeTypeEnum of(Integer code) {
        if (null == code) {
            return null;
        }

        for (PipeTypeEnum pipeTypeEnum : PipeTypeEnum.values()) {
            if (pipeTypeEnum.code.equals(code)) {
                return pipeTypeEnum;
            }
        }
        return null;
    }



    public final Integer code;

    public final String desc;

}
