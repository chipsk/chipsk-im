package com.chipsk.im.client.service.impl;


import com.chipsk.im.client.dto.Info;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class ClientInfo {

    private final Info info = new Info();

    public Info get() {
        return info;
    }

    public ClientInfo saveUserInfo(long userId,String userName){
        info.setUserId(userId);
        info.setUserName(userName);
        return this;
    }


    public ClientInfo saveServiceInfo(String serviceInfo){
        info.setServiceInfo(serviceInfo);
        return this;
    }

    public ClientInfo saveStartDate(){
        info.setStartDate(new Date());
        return this;
    }

}
