package com.chipsk.im.client.service.impl;

import com.chipsk.im.client.client.IMClient;
import com.chipsk.im.client.thread.ContextHolder;
import com.chipsk.im.common.handle.HeartBeatHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientHeartBeatServiceImpl implements HeartBeatHandler {

    private IMClient imClient;

    @Override
    public void process(ChannelHandlerContext ctx) throws Exception {
        //重连
        ContextHolder.setReconnect(true);
        imClient.reconnect();
    }
}
