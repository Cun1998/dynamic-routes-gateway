package com.bestlink.gateway.versionhandler;

import com.bestlink.gateway.entity.Route;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;

/**
 * @author ChuCun JH050169
 * @date 2022/9/5
 */
public abstract class VersionHandler implements Ordered {


    protected VersionHandler successor;

    /**
     * 确定后继
     *
     * @param successor 后继
     */
    public void setSuccessor(VersionHandler successor) {
        this.successor = successor;
    }


    public boolean handleRequest(Route route, HttpHeaders headers) {
        boolean result = this.doHandleRequest(route, headers);
        if (result && successor != null) {
            result = successor.handleRequest(route, headers);
        }
        return result;
    }


    protected abstract boolean doHandleRequest(Route route, HttpHeaders headers);
}
