package com.chipsk.im.client.service.impl;

import org.springframework.stereotype.Component;

@Component
public class ShutDownMsg {
    private boolean isCommand ;

    /**
     * 置为用户主动退出状态
     */
    public void shutdown(){
        isCommand = true ;
    }

    public boolean checkStatus(){
        return isCommand ;
    }
}
