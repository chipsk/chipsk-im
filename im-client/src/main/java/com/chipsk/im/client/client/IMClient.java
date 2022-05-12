package com.chipsk.im.client.client;

import com.chipsk.im.client.config.AppConfiguration;
import com.chipsk.im.client.dto.ServerInfo;
import com.chipsk.im.client.service.EchoService;
import com.chipsk.im.client.service.MsgService;
import com.chipsk.im.client.init.IMClientHandlerInitializer;
import com.chipsk.im.client.service.ReConnectService;
import com.chipsk.im.client.service.RouteReqService;
import com.chipsk.im.client.service.impl.ClientInfo;
import com.chipsk.im.client.thread.ContextHolder;
import com.chipsk.im.client.vo.req.GoogleProtocolVO;
import com.chipsk.im.client.vo.req.LoginReqVO;
import com.chipsk.im.common.constant.Constants;
import com.chipsk.im.common.protocol.IMRequestProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;

@Slf4j
@Component
public class IMClient implements Serializable {

    @Value("${im.user.id}")
    private long userId;

    @Value("${im.user.userName}")
    private String userName;

    private SocketChannel channel;

    @Autowired
    private ClientInfo clientInfo;

    @Autowired
    private AppConfiguration configuration;

    @Autowired
    private MsgService msgService;

    @Autowired
    private EchoService echoService ;

    @Autowired
    private RouteReqService routeReqService;

    @Autowired
    private ReConnectService reConnectService;


    @PostConstruct
    public void start() {
        //登录 + 获取可以使用的服务器 ip+port
        ServerInfo imServer = userLogin();

        //启动客户端
        startClient(imServer);

        //向服务器注册
        loginIMServer();
    }


    /**
     * 登录+路由服务器
     *
     * @return 路由服务器信息
     * @throws Exception
     */
    private ServerInfo userLogin() {
        LoginReqVO loginReqVO = new LoginReqVO(userId, userName);
        ServerInfo imServer = null;
        try {

            //imServer : {"ip":"127.0.0.1","port":8081}
            imServer = routeReqService.getIMServer(loginReqVO);

            //保存系统信息
            clientInfo.saveServiceInfo(imServer.getIp() + ":" + imServer.getImServerPort())
                    .saveUserInfo(userId, userName);
        } catch (Exception e) {
            errCount++;

            if (errCount >= configuration.getErrorCount()) {
                echoService.echo("The maximum number of reconnections has been " +
                        "reached[{}]times, close im client", errCount);
                // 关闭客户端
                msgService.shutdown();
            }
            log.error("login fail", e);
        }
        return imServer;
    }

    /**
     * 向服务器注册
     */
    private void loginIMServer() {
        IMRequestProto.IMReqProtocol login = IMRequestProto.IMReqProtocol.newBuilder()
                .setRequestId(userId)
                .setReqMsg(userName)
                .setType(Constants.CommandType.LOGIN)
                .build();
        ChannelFuture future = channel.writeAndFlush(login);
        future.addListener((ChannelFutureListener) channelFuture -> echoService.echo("Registry im server success!"));
    }

    private int errCount;

    EventLoopGroup group = new NioEventLoopGroup(0, new DefaultThreadFactory("im-work"));

    /**
     * 启动客户端
     * @param imServer
     */
    private void startClient(ServerInfo imServer) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new IMClientHandlerInitializer());

        ChannelFuture future = null;
        try {
            future = bootstrap.connect(imServer.getIp(), imServer.getImServerPort()).sync();
//            future = bootstrap.connect("127.0.0.1", 7070).sync();
        } catch (InterruptedException e) {
            errCount++;

            if (errCount >= configuration.getErrorCount()) {
                log.error("连接失败次数达到上限[{}]次", errCount);
                //关闭
                msgService.shutdown();
            }
            log.error("连接失败", e);
        }
        if (future.isSuccess()) {
            echoService.echo("Start im client success!");
            log.info("启动 im client 成功");
        }
        channel = (SocketChannel) future.channel();
    }


    /**
     * 关闭
     * @throws InterruptedException
     */
    public void close() throws InterruptedException {
        if (channel != null){
            channel.close();
        }
    }

    /**
     * 发送消息字符串
     * @param msg
     */
    public void sendStringMsg(String msg) {
        ByteBuf message = Unpooled.buffer(msg.getBytes().length);
        message.writeBytes(msg.getBytes());
        ChannelFuture future = channel.writeAndFlush(message);
        future.addListener((ChannelFutureListener) channelFuture -> log.info("客户端手动发送消息成功={}", msg));
    }


    /**
     * 发送 Google Protocol 编解码字符串
     *
     * @param googleProtocolVO
     */
    public void sendGoogleProtocolMsg(GoogleProtocolVO googleProtocolVO) {

        IMRequestProto.IMReqProtocol protocol = IMRequestProto.IMReqProtocol.newBuilder()
                .setRequestId(googleProtocolVO.getRequestId())
                .setReqMsg(googleProtocolVO.getMsg())
                .setType(Constants.CommandType.MSG)
                .build();


        ChannelFuture future = channel.writeAndFlush(protocol);
        future.addListener((ChannelFutureListener) channelFuture ->
                log.info("客户端手动发送 Google Protocol 成功={}", googleProtocolVO.toString()));

    }

    /**
     * 1. clear route information.
     * 2. reconnect.
     * 3. shutdown reconnect job.
     * 4. reset reconnect state.
     * @throws Exception
     */
    public void reconnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        // 首先清除路由信息, 下线
        routeReqService.offLine();

        echoService.echo("im server shutdown, reconnecting...");
        start();
        echoService.echo("Great!, reconnect success!");
        reConnectService.reConnectSuccess();
        ContextHolder.clear();
    }


}
