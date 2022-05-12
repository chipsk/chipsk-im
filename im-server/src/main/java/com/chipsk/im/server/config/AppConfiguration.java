package com.chipsk.im.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Data
@Component
public class AppConfiguration {

    @Value("${app.zk.root}")
    private String zkRoot;

    @Value("${app.zk.address}")
    private String zkAddress;

    @Value("${app.zk.switch}")
    private boolean zkSwitch;

    @Value("${im.server.port}")
    private int imServerPort;

    @Value("${im.route.url}")
    private String routeUrl ;

    @Value("${im.heartbeat.time}")
    private long heartBeatTime ;

    @Value("${app.zk.connect.timeout}")
    private int zkConnectTimeout;

    public boolean isZkSwitch() {
        return zkSwitch;
    }
}
