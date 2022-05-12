package com.chipsk.im.server.api.vo;

import com.chipsk.im.server.api.vo.req.SendMsgReqVO;

public interface ServerApi {

    /**
     * Push msg to client
     * @param sendMsgReqVO
     * @return
     * @throws Exception
     */
    Object sendMsg(SendMsgReqVO sendMsgReqVO) throws Exception;
}
