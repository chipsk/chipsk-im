package com.chipsk.im.route.api.vo.res;

import com.chipsk.im.common.pojo.RouteInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class IMServerResVO implements Serializable {

    private String ip ;
    private Integer imServerPort;
    private Integer httpPort;

    public IMServerResVO(RouteInfo routeInfo) {
        this.ip = routeInfo.getIp();
        this.imServerPort = routeInfo.getImServerPort();
        this.httpPort = routeInfo.getHttpPort();
    }
}
