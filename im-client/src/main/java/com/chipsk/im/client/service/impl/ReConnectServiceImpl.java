package com.chipsk.im.client.service.impl;

import com.chipsk.im.client.service.ReConnectService;
import com.chipsk.im.client.thread.ReConnectJob;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


/**
 * TODO ScheduledExecutorService待研究
 */
@Service
public class ReConnectServiceImpl implements ReConnectService {

    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public void reConnect(ChannelHandlerContext ctx) {
        buildExecutor() ;
        scheduledExecutorService.scheduleAtFixedRate(new ReConnectJob(ctx),0,10, TimeUnit.SECONDS) ;
    }

    @Override
    public void reConnectSuccess() {
        scheduledExecutorService.shutdown();
    }


    @Override
    public ScheduledExecutorService buildExecutor() {
        if (scheduledExecutorService == null || scheduledExecutorService.isShutdown()) {
            ThreadFactory schedule = new ThreadFactoryBuilder()
                    .setNameFormat("reConnect-job-%d")
                    .setDaemon(true)
                    .build();
            scheduledExecutorService = new ScheduledThreadPoolExecutor(1, schedule);
            return scheduledExecutorService;
        } else {
            return scheduledExecutorService;
        }
    }
}
