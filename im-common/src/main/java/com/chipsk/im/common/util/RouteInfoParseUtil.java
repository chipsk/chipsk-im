package com.chipsk.im.common.util;

import com.chipsk.im.common.exception.IMException;
import com.chipsk.im.common.pojo.RouteInfo;

import static com.chipsk.im.common.enums.StatusEnum.VALIDATION_FAIL;

public class RouteInfoParseUtil {

    public static RouteInfo parse(String info){
        try {
            String[] serverInfo = info.split(":");
            return new RouteInfo(serverInfo[0], Integer.parseInt(serverInfo[1]),Integer.parseInt(serverInfo[2]));
        }catch (Exception e){
            throw new IMException(VALIDATION_FAIL) ;
        }
    }
}
