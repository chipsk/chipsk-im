package com.chipsk.im.client.thread;


import com.chipsk.im.client.service.impl.ClientHeartBeatServiceImpl;
import com.chipsk.im.client.utils.SpringBeanFactory;
import com.chipsk.im.common.handle.HeartBeatHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReConnectJob implements Runnable {

    private ChannelHandlerContext context ;

    private HeartBeatHandler heartBeatHandler ;

    public ReConnectJob(ChannelHandlerContext context) {
        this.context = context;
        this.heartBeatHandler = SpringBeanFactory.getBean(ClientHeartBeatServiceImpl.class) ;
    }

    @Override
    public void run() {
        try {
            heartBeatHandler.process(context);
        } catch (Exception e) {
            log.error("Exception",e);
        }
    }
}
