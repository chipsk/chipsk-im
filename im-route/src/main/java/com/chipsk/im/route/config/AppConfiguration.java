package com.chipsk.im.route.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppConfiguration {

    @Value("${app.zk.root}")
    private String zkRoot;

    @Value("${app.zk.address}")
    private String zkAddress;


    @Value("${server.port}")
    private int port;

    @Value("${app.zk.connect.timeout}")
    private int zkConnectTimeout;

    @Value("${app.route.way}")
    private String routeWay;

//    @Value("${app.route.way.consitenthash}")
//    private String consistentHashWay;

}
