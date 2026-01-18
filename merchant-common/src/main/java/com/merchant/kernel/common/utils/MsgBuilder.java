package com.merchant.kernel.common.utils;

public class MsgBuilder {
    private MsgBuilder() {
    }

    public static String build(String template, Object... args) {
        if (template == null) {
            return null;
        }
        String format = template.replace("{}", "%s");
        return String.format(format, args);
    }
}
