package com.bestlink.gateway.versionhandler;

import com.bestlink.gateway.entity.Route;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ChuCun JH050169
 * @date 2022/9/5
 */
@Component
public class UserVersionHandler extends VersionHandler {


    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    protected boolean doHandleRequest(Route route, HttpHeaders headers) {
        List<String> userList = headers.get("current-user");
        List<String> routeUserList = route.getUser();
        if (routeUserList != null && !routeUserList.isEmpty()) {
            if (!routeUserList.get(0).equalsIgnoreCase("*") && userList != null && !userList.isEmpty()
                    && routeUserList.stream().anyMatch(e -> e.equalsIgnoreCase(userList.get(0)))) {
                return true;
            } else return routeUserList.get(0).equalsIgnoreCase("*") && (userList == null || userList.isEmpty());
        }
        return false;
    }
}
