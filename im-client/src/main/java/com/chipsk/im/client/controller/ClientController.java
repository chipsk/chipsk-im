package com.chipsk.im.client.controller;


import com.chipsk.im.client.client.IMClient;
import com.chipsk.im.client.constant.Constants;
import com.chipsk.im.client.service.RouteReqService;
import com.chipsk.im.client.vo.req.GoogleProtocolVO;
import com.chipsk.im.client.vo.req.GroupReqVO;
import com.chipsk.im.client.vo.req.StringReqVO;
import com.chipsk.im.client.vo.res.SendMsgResVO;
import com.chipsk.im.common.res.BaseResponse;
import com.chipsk.im.common.res.NULLBody;
import com.chipsk.im.server.api.vo.req.SendMsgReqVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.web.bind.annotation.*;

@RestController
public class ClientController {

    /**
     * 使用spring boot自动装配相关类
     * 统计 service
     */
    @Autowired
    private CounterService counterService;

    @Autowired
    private IMClient imClient;

    @Autowired
    private RouteReqService routeReqService;


    /**
     * 向服务端发消息 字符串
     *
     * @param stringReqVO
     * @return
     */
    @ApiOperation("客户端发送消息，字符串")
    @PostMapping("/sendStringMsg")
    public BaseResponse<NULLBody> sendStringMsg(@RequestBody StringReqVO stringReqVO) {
        for (int i = 0; i < 100; i++) {
            imClient.sendStringMsg(stringReqVO.getMsg());
        }
        // 利用 actuator 来自增
        counterService.increment(Constants.COUNTER_CLIENT_PUSH_COUNT);

        SendMsgResVO sendMsgResVO = new SendMsgResVO();
        sendMsgResVO.setMsg("OK, 发送成功");
        return BaseResponse.okResult();
    }

    /**
     * 向服务端发消息 Google ProtoBuf
     *
     * @param googleProtocolVO
     * @return
     */
    @ApiOperation("向服务端发消息 Google ProtoBuf")
    @PostMapping("/sendProtoBufMsg")
    public BaseResponse<NULLBody> sendProtoBufMsg(@RequestBody GoogleProtocolVO googleProtocolVO) {

        for (int i = 0; i < 100; i++) {
            imClient.sendGoogleProtocolMsg(googleProtocolVO);
        }

        // 利用 actuator 来自增
        counterService.increment(Constants.COUNTER_CLIENT_PUSH_COUNT);

        SendMsgResVO sendMsgResVO = new SendMsgResVO();
        sendMsgResVO.setMsg("OK, 发送成功");
        return BaseResponse.okResult();
    }

    /**
     * 群发消息
     *
     * @param sendMsgReqVO
     * @return
     */
    @ApiOperation("群发消息")
    @PostMapping("/sendGroupMsg")
    @ResponseBody
    public BaseResponse<NULLBody> sendGroupMsg(@RequestBody SendMsgReqVO sendMsgReqVO) throws Exception {

        GroupReqVO groupReqVO = new GroupReqVO(sendMsgReqVO.getUserId(), sendMsgReqVO.getMsg());
        routeReqService.sendGroupMsg(groupReqVO);

        counterService.increment(Constants.COUNTER_SERVER_PUSH_COUNT);

        return BaseResponse.okResult();
    }


}
