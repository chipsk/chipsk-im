package com.chipsk.im.client.vo.res;

import com.chipsk.im.client.dto.ServerInfo;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class IMServerResVO implements Serializable {

    /**
     * code : 9000
     * message : 成功
     * reqNo : null
     * dataBody : {"ip":"127.0.0.1","port":8081}
     */

    private String code;

    private String message;

    private Object reqNo;

    private ServerInfo dataBody;


}
