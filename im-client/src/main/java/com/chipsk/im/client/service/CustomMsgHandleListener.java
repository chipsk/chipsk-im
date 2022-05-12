package com.chipsk.im.client.service;

/**
 * 自定义消息回调
 */
public interface CustomMsgHandleListener {

    /**
     * 消息回调
     * @param msg
     */
    void handle(String msg);
}
