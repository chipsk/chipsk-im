package com.chipsk.im.client.service;

import com.chipsk.im.client.dto.ServerInfo;
import com.chipsk.im.client.vo.req.GroupReqVO;
import com.chipsk.im.client.vo.req.LoginReqVO;
import com.chipsk.im.client.vo.req.P2PReqVO;
import com.chipsk.im.client.vo.res.OnlineUsersResVO;

import java.util.List;

public interface RouteReqService {

    /**
     * 群发消息
     * @param groupReqVO 消息
     * @throws Exception
     */
    void sendGroupMsg(GroupReqVO groupReqVO) throws Exception;

    /**
     * 私聊
     * @param p2PReqVO
     * @throws Exception
     */
    void sendP2PMsg(P2PReqVO p2PReqVO)throws Exception;

    /**
     * 获取服务器
     * @return 服务ip+port
     * @param loginReqVO
     * @throws Exception
     */
    ServerInfo getIMServer(LoginReqVO loginReqVO) throws Exception;

    /**
     * 获取所有在线用户
     * @return
     * @throws Exception
     */
    List<OnlineUsersResVO.DataBodyBean> onlineUsers()throws Exception ;


    void offLine() ;
}
