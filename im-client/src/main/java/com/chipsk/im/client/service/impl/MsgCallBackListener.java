package com.chipsk.im.client.service.impl;

import com.chipsk.im.client.service.CustomMsgHandleListener;
import com.chipsk.im.client.service.MsgLogService;
import com.chipsk.im.client.utils.SpringBeanFactory;

public class MsgCallBackListener implements CustomMsgHandleListener {

    private final MsgLogService msgLogService;

    public MsgCallBackListener() {
        this.msgLogService = SpringBeanFactory.getBean(MsgLogService.class) ;
    }

    @Override
    public void handle(String msg) {
        msgLogService.log(msg);
    }
}
