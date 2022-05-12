package com.chipsk.im.client.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@Data
@Component
public class AppConfiguration {

    @Value("${im.user.id}")
    private Long userId;

    @Value("${im.user.userName}")
    private String userName;

    @Value("${im.msg.logger.path}")
    private String msgLoggerPath ;

//    @Value("${im.clear.route.request.url}")
//    private String clearRouteUrl ;

    @Value("${im.heartbeat.time}")
    private long heartBeatTime ;

    @Value("${im.reconnect.count}")
    private int errorCount ;

}
