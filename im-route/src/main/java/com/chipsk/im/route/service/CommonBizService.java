package com.chipsk.im.route.service;

import com.chipsk.im.common.enums.StatusEnum;
import com.chipsk.im.common.exception.IMException;
import com.chipsk.im.common.pojo.RouteInfo;
import com.chipsk.im.route.cache.ServerCache;
import com.chipsk.im.route.kit.NetAddressIsReachableKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommonBizService {

    @Autowired
    private ServerCache serverCache ;

    /**
     * check ip and port
     * @param routeInfo
     */
    public void checkServerAvailable(RouteInfo routeInfo){
        boolean reachable = NetAddressIsReachableKit.checkAddressReachable(routeInfo.getIp(), routeInfo.getImServerPort(), 1000);
        if (!reachable) {
            log.error("ip={}, port={} are not available", routeInfo.getIp(), routeInfo.getImServerPort());

            // rebuild cache
            serverCache.rebuildCacheList();

            throw new IMException(StatusEnum.SERVER_NOT_AVAILABLE) ;
        }
    }
}
