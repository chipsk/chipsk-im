package com.chipsk.im.server.controller;

import com.chipsk.im.common.constant.Constants;
import com.chipsk.im.common.res.BaseResponse;
import com.chipsk.im.server.api.vo.ServerApi;
import com.chipsk.im.server.api.vo.req.SendMsgReqVO;
import com.chipsk.im.server.api.vo.res.SendMsgResVO;
import com.chipsk.im.server.server.IMServer;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ServerController implements ServerApi {

    @Autowired
    private IMServer imServer;

    /**
     * 统计 service
     */
    @Autowired
    private CounterService counterService;

    @ApiOperation("Push msg to client")
    @PostMapping("/sendMsg")
    @Override
    public BaseResponse<SendMsgResVO> sendMsg(@RequestBody SendMsgReqVO sendMsgReqVO) throws Exception {
        imServer.sendMsg(sendMsgReqVO);

        counterService.increment(Constants.COUNTER_SERVER_PUSH_COUNT);

        SendMsgResVO sendMsgResVO = new SendMsgResVO();
        sendMsgResVO.setMsg("OK");

        return BaseResponse.okResult(sendMsgResVO);
    }
}
