package com.chipsk.im.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RouteInfo {

    private String ip ;

    private Integer imServerPort;

    private Integer httpPort;

}
