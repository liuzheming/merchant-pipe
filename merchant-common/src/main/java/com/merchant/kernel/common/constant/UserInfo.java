package com.merchant.kernel.common.constant;

public class UserInfo {
    private Long ucId;
    private String name;

    public UserInfo() {
    }

    public UserInfo(Long ucId, String name) {
        this.ucId = ucId;
        this.name = name;
    }

    public Long getUcId() {
        return ucId;
    }

    public void setUcId(Long ucId) {
        this.ucId = ucId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
