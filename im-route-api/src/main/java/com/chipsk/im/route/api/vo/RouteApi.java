package com.chipsk.im.route.api.vo;

import com.chipsk.im.common.res.BaseResponse;
import com.chipsk.im.route.api.vo.req.ChatReqVo;
import com.chipsk.im.route.api.vo.req.LoginReqVO;
import com.chipsk.im.route.api.vo.req.P2PReqVO;
import com.chipsk.im.route.api.vo.req.RegisterInfoReqVO;
import com.chipsk.im.route.api.vo.res.RegisterInfoResVO;

public interface RouteApi {
    /**
     * group chat
     *
     * @param groupReqVO
     * @return
     * @throws Exception
     */
    Object groupRoute(ChatReqVo groupReqVO) throws Exception;

    /**
     * Point to point chat
     * @param p2pRequest
     * @return
     * @throws Exception
     */
    Object p2pRoute(P2PReqVO p2pRequest) throws Exception;


    /**
     * Offline account
     *
     * @param groupReqVO
     * @return
     * @throws Exception
     */
    Object offLine(ChatReqVo groupReqVO) throws Exception;

    /**
     * Login account
     * @param loginReqVO
     * @return
     * @throws Exception
     */
    Object login(LoginReqVO loginReqVO) throws Exception;

    /**
     * Register account
     *
     * @param registerInfoReqVO
     * @return
     * @throws Exception
     */
    BaseResponse<RegisterInfoResVO> registerAccount(RegisterInfoReqVO registerInfoReqVO) throws Exception;

    /**
     * Get all online users
     *
     * @return
     * @throws Exception
     */
    Object onlineUser() throws Exception;
}
