package com.chipsk.im.server.sevice;

import com.chipsk.im.common.pojo.IMUserInfo;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;

public interface RouteService {

    /**
     * 用户下线
     *
     * @param userInfo
     * @param channel
     * @throws IOException
     */
    void userOffLine(IMUserInfo userInfo, NioSocketChannel channel);


    /**
     * 清除路由关系
     *
     * @param userInfo
     * @throws IOException
     */
    void clearRouteInfo(IMUserInfo userInfo);
}
