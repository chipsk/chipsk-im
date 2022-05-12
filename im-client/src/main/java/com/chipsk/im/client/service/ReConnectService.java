package com.chipsk.im.client.service;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ScheduledExecutorService;


public interface ReConnectService {


    /**
     * Trigger reconnect job
     * @param ctx
     */
    void reConnect(ChannelHandlerContext ctx);

    /**
     * Close reconnect job if reconnect success.
     */
    void reConnectSuccess();

    /***
     * build an thread executor
     * @return
     */
    ScheduledExecutorService buildExecutor();

}

