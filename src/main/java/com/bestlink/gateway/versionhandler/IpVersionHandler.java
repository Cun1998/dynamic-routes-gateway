package com.bestlink.gateway.versionhandler;

import com.bestlink.gateway.entity.Route;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ChuCun JH050169
 * @date 2022/9/5
 */
@Component
public class IpVersionHandler extends VersionHandler {

    @Override
    public int getOrder() {
        return 2;
    }

    private boolean isIpAvailable(List<String> ipList, String ip) {
        if (ipList.contains(ip)) {
            return true;
        }
        boolean flag = true;
        List<String> ipSegmentList = ipList.stream().filter(e -> e.contains("-")).collect(Collectors.toList());
        for (String ipSegment : ipSegmentList) {
            String[] ips = ipSegment.split("-");
            for (int i = 0; i < 4; i++) {
                String[] segment = ip.split("\\.");
                if (Integer.parseInt(segment[i]) < Integer.parseInt(ips[0].split("\\.")[i])
                        || Integer.parseInt(segment[i]) > Integer.parseInt(ips[1].split("\\.")[i])) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    @Override
    protected boolean doHandleRequest(Route route, HttpHeaders headers) {
        List<String> clientIpList = headers.get("clientIp");
        List<String> routeIpList = route.getIp();
        if (routeIpList != null && !routeIpList.isEmpty()) {
            if (!routeIpList.get(0).equalsIgnoreCase("*") && clientIpList != null
                    && isIpAvailable(routeIpList, clientIpList.get(0))) {
                return true;
            } else return routeIpList.get(0).equalsIgnoreCase("*");
        }
        return false;
    }
}
