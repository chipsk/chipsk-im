package com.chipsk.im.server.server;


import com.chipsk.im.common.constant.Constants;
import com.chipsk.im.common.protocol.IMRequestProto;
import com.chipsk.im.server.api.vo.req.SendMsgReqVO;
import com.chipsk.im.server.init.IMChannelInitializer;
import com.chipsk.im.server.utils.SessionSocketHolder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Component
@Slf4j
public class IMServer {

    @Value("${im.server.port}")
    private int nettyPort;

    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup work = new NioEventLoopGroup();


    @PostConstruct
    public void start() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(nettyPort))
                //保持长连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new IMChannelInitializer());

        ChannelFuture future = bootstrap.bind().sync();

        if (future.isSuccess()) {
            log.info("Start cim server success!!!");
        }

    }

    /**
     * Push msg to client.
     * @param sendMsgReqVO 消息
     */
    public void sendMsg(SendMsgReqVO sendMsgReqVO) {
        NioSocketChannel socketChannel = SessionSocketHolder.get(sendMsgReqVO.getUserId());

        if (null == socketChannel) {
            log.error("client {} offline!", sendMsgReqVO.getUserId());
            return;
        }
        IMRequestProto.IMReqProtocol protocol = IMRequestProto.IMReqProtocol.newBuilder()
                .setRequestId(sendMsgReqVO.getUserId())
                .setReqMsg(sendMsgReqVO.getMsg())
                .setType(Constants.CommandType.MSG)
                .build();

        ChannelFuture future = socketChannel.writeAndFlush(protocol);
        future.addListener((ChannelFutureListener) future1 -> log.info("server push msg:[{}]", sendMsgReqVO.toString()));

    }


}
