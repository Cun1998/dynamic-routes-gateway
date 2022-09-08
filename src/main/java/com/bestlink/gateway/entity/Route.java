package com.bestlink.gateway.entity;

import java.util.List;

/**
 * @author ChuCun JH050169
 * @date 2022/9/6
 */

public class Route {


    private String version;

    private List<String> user;
    private List<String> ip;
    private List<String> header;

    public Route(String version, List<String> user, List<String> ip, List<String> header) {
        this.version = version;
        this.user = user;
        this.ip = ip;
        this.header = header;
    }

    public Route() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getUser() {
        return user;
    }

    public void setUser(List<String> user) {
        this.user = user;
    }

    public List<String> getIp() {
        return ip;
    }

    public void setIp(List<String> ip) {
        this.ip = ip;
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }


}
