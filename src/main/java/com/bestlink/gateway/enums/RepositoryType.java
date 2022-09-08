package com.bestlink.gateway.enums;

/**
 * @author ChuCun JH050169
 * @date 2022/9/6
 */
public enum RepositoryType {

    NACOS("nacos"), REDIS("redis");

    private final String type;

    RepositoryType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return getType();
    }


}
