package com.bestlink.gateway.config;

/**
 * nacos 配置 信息
 */
@SuppressWarnings("rawtypes")
public class NacosConfigInfo {
    private String serverAddr;
    private String namespace;
    private String group;
    private String dataId;
    private boolean refresh = true;
    private Class cls = String.class;
    public NacosConfigInfo(String serverAddr, String namespace, String group, String dataId, boolean refresh, Class cls) {
        this.serverAddr = serverAddr;
        this.namespace = namespace;
        this.group = group;
        this.dataId = dataId;
        this.refresh = refresh;
        this.cls = cls;
    }
    public NacosConfigInfo() {
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public Class getCls() {
        return cls;
    }

    public void setCls(Class cls) {
        this.cls = cls;
    }

    public long getTimeout() {
        return 5000L;
    }
}
