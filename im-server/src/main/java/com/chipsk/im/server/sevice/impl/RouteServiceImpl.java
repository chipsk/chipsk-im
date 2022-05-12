package com.chipsk.im.server.sevice.impl;

import com.chipsk.im.common.pojo.IMUserInfo;
import com.chipsk.im.common.proxy.ProxyManager;
import com.chipsk.im.route.api.vo.RouteApi;
import com.chipsk.im.route.api.vo.req.ChatReqVo;
import com.chipsk.im.server.config.AppConfiguration;
import com.chipsk.im.server.sevice.RouteService;
import com.chipsk.im.server.utils.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class RouteServiceImpl implements RouteService {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private AppConfiguration configuration;


    @Override
    public void userOffLine(IMUserInfo userInfo, NioSocketChannel channel) {
        if (userInfo != null) {
            log.info("Account [{}] offline", userInfo.getUserName());
            SessionSocketHolder.removeSession(userInfo.getUserId());
            //清楚路由关系
            clearRouteInfo(userInfo);
        }
        SessionSocketHolder.remove(channel);
    }

    @Override
    public void clearRouteInfo(IMUserInfo userInfo) {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, configuration.getRouteUrl(), okHttpClient).getInstance();
        Response response = null;
        ChatReqVo offlineVo = new ChatReqVo(userInfo.getUserId(), userInfo.getUserName());
        try {
            response = (Response) routeApi.offLine(offlineVo);
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            response.body().close();
        }
    }


}
