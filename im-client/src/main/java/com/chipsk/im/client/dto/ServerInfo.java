package com.chipsk.im.client.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ServerInfo {

    /**
     * ip : 127.0.0.1
     * port : 8081
     */
    private String ip ;

    private Integer imServerPort;

    private Integer httpPort;

}
