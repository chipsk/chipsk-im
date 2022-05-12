package com.chipsk.im.server.handle;

import com.chipsk.im.common.constant.Constants;
import com.chipsk.im.common.handle.HeartBeatHandler;
import com.chipsk.im.common.pojo.IMUserInfo;
import com.chipsk.im.common.protocol.IMRequestProto;
import com.chipsk.im.common.util.NettyAttrUtil;
import com.chipsk.im.server.sevice.impl.RouteServiceImpl;
import com.chipsk.im.server.sevice.impl.ServerHeartBeatServiceImpl;
import com.chipsk.im.server.utils.SessionSocketHolder;
import com.chipsk.im.server.utils.SpringBeanFactory;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class IMServerHandle extends SimpleChannelInboundHandler<IMRequestProto.IMReqProtocol> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMRequestProto.IMReqProtocol msg) throws Exception {
        log.info("received msg=[{}]", msg.toString());

        if (msg.getType() == Constants.CommandType.LOGIN) {
            //保持客户端与 channel 之间的关系
            SessionSocketHolder.put(msg.getRequestId(), (NioSocketChannel) ctx.channel());
            SessionSocketHolder.saveSession(msg.getRequestId(), msg.getReqMsg());
            log.info("client [{}] online success!", msg.getReqMsg());
        }

        //心跳更新时间
        if (msg.getType() == Constants.CommandType.PING) {
            NettyAttrUtil.updateReaderTime(ctx.channel(), System.currentTimeMillis());
            //向客户端响应ping信息
            IMRequestProto.IMReqProtocol heartBeat = SpringBeanFactory.getBean("heartBeat",
                    IMRequestProto.IMReqProtocol.class);
            ctx.writeAndFlush(heartBeat).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.error("IO error, close Channel");
                    future.channel().close();
                }
            });
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {

                log.info("定时检测客户端端是否存活");

                HeartBeatHandler heartBeatHandler = SpringBeanFactory.getBean(ServerHeartBeatServiceImpl.class) ;
                heartBeatHandler.process(ctx) ;
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //可能出现业务判断离线后再次触发 channelInactive
        IMUserInfo userInfo = SessionSocketHolder.getUserId((NioSocketChannel) ctx.channel());
        if (userInfo != null){
            log.warn("[{}] trigger channelInactive offline!",userInfo.getUserName());

            //Clear route info and offline.
            RouteServiceImpl routeService = SpringBeanFactory.getBean(RouteServiceImpl.class);
            routeService.userOffLine(userInfo,(NioSocketChannel) ctx.channel());

            ctx.channel().close();
        }
    }
}
