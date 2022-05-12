package com.chipsk.im.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.chipsk.im.client.config.AppConfiguration;
import com.chipsk.im.client.dto.ServerInfo;
import com.chipsk.im.client.service.EchoService;
import com.chipsk.im.client.service.RouteReqService;
import com.chipsk.im.client.thread.ContextHolder;
import com.chipsk.im.client.vo.req.GroupReqVO;
import com.chipsk.im.client.vo.req.LoginReqVO;
import com.chipsk.im.client.vo.req.P2PReqVO;
import com.chipsk.im.client.vo.res.IMServerResVO;
import com.chipsk.im.client.vo.res.OnlineUsersResVO;
import com.chipsk.im.common.enums.StatusEnum;
import com.chipsk.im.common.exception.IMException;
import com.chipsk.im.common.proxy.ProxyManager;
import com.chipsk.im.common.res.BaseResponse;
import com.chipsk.im.route.api.vo.RouteApi;

import com.chipsk.im.route.api.vo.req.ChatReqVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
@Slf4j
public class RouteReqServiceImpl implements RouteReqService, Serializable {


    @Autowired
    private OkHttpClient okHttpClient;

    @Value("${im.route.url}")
    private String routeUrl;

    @Autowired
    private EchoService echoService;


    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public void sendGroupMsg(GroupReqVO groupReqVO) throws Exception {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, routeUrl, okHttpClient).getInstance();
        ChatReqVo chatReqVo = new ChatReqVo(groupReqVO.getUserId(), groupReqVO.getMsg());
        Response response = null;
        try {
            response = (Response) routeApi.groupRoute(chatReqVo);
        } catch (Exception e) {
            log.error("exception", e);
        } finally {
            response.body().close();
        }
    }

    @Override
    public void sendP2PMsg(P2PReqVO p2PReqVO) throws Exception {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, routeUrl, okHttpClient).getInstance();
        com.chipsk.im.route.api.vo.req.P2PReqVO vo = new com.chipsk.im.route.api.vo.req.P2PReqVO();
        vo.setMsg(p2PReqVO.getMsg());
        vo.setReceiveUserId(p2PReqVO.getReceiveUserId());
        vo.setUserId(p2PReqVO.getUserId());

        Response response = null;
        try {
            response = (Response) routeApi.p2pRoute(vo);
            String json = response.body().string();
            BaseResponse baseResponse = JSON.parseObject(json, BaseResponse.class);

            // account offline
            if (baseResponse.getCode().equals(StatusEnum.OFF_LINE.getCode())) {
                log.error(p2PReqVO.getReceiveUserId() + ":" + StatusEnum.OFF_LINE.getMessage());
            }
        } catch (Exception e) {
            log.error("exception",e);
        } finally {
            response.body().close();
        }
    }

    @Override
    public ServerInfo getIMServer(LoginReqVO loginReqVO) throws Exception {
        // 动态代理获取 routeApi
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, routeUrl, okHttpClient).getInstance();
        //bean拷贝
        com.chipsk.im.route.api.vo.req.LoginReqVO vo = new com.chipsk.im.route.api.vo.req.LoginReqVO();
        vo.setUserId(loginReqVO.getUserId());
        vo.setUserName(loginReqVO.getUserName());

        Response response = null;
        IMServerResVO imServerResVO = null;

        try {
            response = (Response) routeApi.login(vo);
            String json = response.body().string();
             imServerResVO = JSON.parseObject(json, IMServerResVO.class);

            //重复失败
            if (!imServerResVO.getCode().equals(StatusEnum.SUCCESS.getCode())) {
                echoService.echo(imServerResVO.getMessage());

                // when client in reConnect state, could not exit.
                if (ContextHolder.getReconnect()){
                    echoService.echo("###{}###", StatusEnum.RECONNECT_FAIL.getMessage());
                    throw new IMException(StatusEnum.RECONNECT_FAIL);
                }

                System.exit(-1);
            }
        } catch (Exception e){
            log.error("exception",e);
        }finally {
            response.body().close();
        }

        return imServerResVO.getDataBody();
    }

    @Override
    public List<OnlineUsersResVO.DataBodyBean> onlineUsers() throws Exception {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, routeUrl, okHttpClient).getInstance();

        Response response = null;
        OnlineUsersResVO onlineUsersResVO = null;
        try {
            response = (Response) routeApi.onlineUser();
            String json = response.body().string() ;
            onlineUsersResVO = JSON.parseObject(json, OnlineUsersResVO.class);

        }catch (Exception e){
            log.error("exception",e);
        }finally {
            response.body().close();
        }

        return onlineUsersResVO.getDataBody();
    }



    @Override
    public void offLine() {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, routeUrl, okHttpClient).getInstance();
        ChatReqVo offLineVo = new ChatReqVo(appConfiguration.getUserId(), "offLine");
        Response response = null;
        try {
            response = (Response) routeApi.offLine(offLineVo);
        } catch (Exception e) {
            log.error("exception", e);
        } finally {
            response.body().close();
        }
    }
}
