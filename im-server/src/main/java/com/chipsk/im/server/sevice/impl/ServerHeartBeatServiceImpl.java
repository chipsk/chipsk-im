package com.chipsk.im.server.sevice.impl;

import com.chipsk.im.common.handle.HeartBeatHandler;
import com.chipsk.im.common.pojo.IMUserInfo;
import com.chipsk.im.common.util.NettyAttrUtil;
import com.chipsk.im.server.config.AppConfiguration;
import com.chipsk.im.server.sevice.RouteService;
import com.chipsk.im.server.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class ServerHeartBeatServiceImpl implements HeartBeatHandler {

    @Autowired
    private RouteService routeService;

    @Autowired
    private AppConfiguration appConfiguration ;

    @Override
    public void process(ChannelHandlerContext ctx) throws Exception {
        long heartBeatTime = appConfiguration.getHeartBeatTime() * 1000;
        Long lastReadTime = NettyAttrUtil.getReaderTime(ctx.channel());
        long now = System.currentTimeMillis();
        if (lastReadTime != null && now - lastReadTime > heartBeatTime) {
            IMUserInfo userInfo = SessionSocketHolder.getUserId((NioSocketChannel) ctx.channel());
            if (userInfo != null) {
                log.warn("客户端[{}]心跳超时[{}]ms, 需要关闭连接", userInfo.getUserName(), now - lastReadTime);
            }
            routeService.userOffLine(userInfo, (NioSocketChannel) ctx.channel());
            ctx.channel().close();
        }
    }
}
