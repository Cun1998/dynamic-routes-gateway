package com.bestlink.gateway.versionhandler;

import com.bestlink.gateway.entity.Route;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ChuCun JH050169
 * @date 2022/9/5
 */
@Component
public class HeaderVersionHandler extends VersionHandler {


    @Override
    public int getOrder() {
        return 3;
    }

    private Map<String, String> getHeaderMap(List<String> headerList) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerList.forEach(header -> {
            String[] split = header.split("=");
            headerMap.put(split[0], split[1]);
        });
        return headerMap;
    }

    @Override
    protected boolean doHandleRequest(Route route, HttpHeaders headers) {
        List<String> headerList = route.getHeader();
        AtomicBoolean available = new AtomicBoolean(false);
        if (headerList != null && !headerList.isEmpty()
                && !headerList.get(0).equalsIgnoreCase("*")) {
            Map<String, String> headerMap = getHeaderMap(headerList);
            headerMap.forEach((k, v) -> {
                List<String> valueList = headers.get(k);
                if (valueList != null && valueList.stream().anyMatch(e -> e.equalsIgnoreCase(v))) {
                    available.set(true);
                }
            });
        } else if (headerList != null && !headerList.isEmpty()
                && headerList.get(0).equalsIgnoreCase("*")) {
            available.set(true);
        }
        return available.get();
    }
}
