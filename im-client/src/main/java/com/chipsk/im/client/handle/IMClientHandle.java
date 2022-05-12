package com.chipsk.im.client.handle;

import com.chipsk.im.client.constant.Constants;
import com.chipsk.im.client.service.EchoService;
import com.chipsk.im.client.service.ReConnectService;
import com.chipsk.im.client.service.impl.EchoServiceImpl;
import com.chipsk.im.client.service.impl.ReConnectServiceImpl;
import com.chipsk.im.client.service.impl.ShutDownMsg;
import com.chipsk.im.client.utils.SpringBeanFactory;
import com.chipsk.im.common.handle.HeartBeatHandler;
import com.chipsk.im.common.protocol.IMRequestProto;
import com.chipsk.im.common.protocol.IMResponseProto;
import com.chipsk.im.common.util.NettyAttrUtil;
import com.chipsk.im.server.sevice.impl.ServerHeartBeatServiceImpl;
import com.vdurmont.emoji.EmojiParser;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@ChannelHandler.Sharable
@Slf4j
public class IMClientHandle extends SimpleChannelInboundHandler<IMResponseProto.IMResProtocol> {

    private EchoService echoService ;

    private MsgHandleCaller caller;

    private ScheduledExecutorService scheduledExecutorService ;

    private ThreadPoolExecutor threadPoolExecutor;

    private ShutDownMsg shutDownMsg;

    private ReConnectService reConnectService;

    /**
     * 回调消息
     * @param msg
     */
    private void callBackMsg(String msg) {
        threadPoolExecutor = SpringBeanFactory.getBean("callBackThreadPool", ThreadPoolExecutor.class) ;
        threadPoolExecutor.execute(() -> {
            caller = SpringBeanFactory.getBean(MsgHandleCaller.class) ;
            caller.getMsgHandleListener().handle(msg);
        });

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt ;

            if (idleStateEvent.state() == IdleState.WRITER_IDLE){
                IMRequestProto.IMReqProtocol heartBeat = SpringBeanFactory.getBean("heartBeat",
                        IMRequestProto.IMReqProtocol.class);
                ctx.writeAndFlush(heartBeat).addListeners((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        log.error("IO error,close Channel");
                        future.channel().close();
                    }
                }) ;
            }

        }
        super.userEventTriggered(ctx, evt);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        if (shutDownMsg == null){
            shutDownMsg = SpringBeanFactory.getBean(ShutDownMsg.class) ;
        }

        //用户主动退出，不执行重连逻辑
        if (shutDownMsg.checkStatus()){
            return;
        }

        if (scheduledExecutorService == null){
            scheduledExecutorService = SpringBeanFactory.getBean("scheduledTask", ScheduledExecutorService.class) ;
            reConnectService = SpringBeanFactory.getBean(ReConnectServiceImpl.class) ;
        }
        log.info("客户端断开了，重新连接！");
        reConnectService.reConnect(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMResponseProto.IMResProtocol msg) throws Exception {
        if (echoService == null) {
            echoService = SpringBeanFactory.getBean(EchoServiceImpl.class);
        }

        //心跳更新时间
        if (msg.getType() == Constants.CommandType.PING) {
            //log.info("收到服务端心跳");
            NettyAttrUtil.updateReaderTime(ctx.channel(), System.currentTimeMillis());
        }

        if (msg.getType() != Constants.CommandType.PING) {
            //回调消息
            callBackMsg(msg.getResMsg());
            String response = EmojiParser.parseToUnicode(msg.getResMsg());
            echoService.echo(response);
        }
    }
}
