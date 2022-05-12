package com.chipsk.im.route.controller;

import com.chipsk.im.common.enums.StatusEnum;
import com.chipsk.im.common.exception.IMException;
import com.chipsk.im.common.pojo.IMUserInfo;
import com.chipsk.im.common.pojo.RouteInfo;
import com.chipsk.im.common.res.BaseResponse;
import com.chipsk.im.common.res.NULLBody;
import com.chipsk.im.common.util.RouteInfoParseUtil;
import com.chipsk.im.route.algorithm.RouteHandle;
import com.chipsk.im.route.api.vo.RouteApi;
import com.chipsk.im.route.api.vo.req.ChatReqVo;
import com.chipsk.im.route.api.vo.req.LoginReqVO;
import com.chipsk.im.route.api.vo.req.P2PReqVO;
import com.chipsk.im.route.api.vo.req.RegisterInfoReqVO;
import com.chipsk.im.route.api.vo.res.IMServerResVO;
import com.chipsk.im.route.api.vo.res.RegisterInfoResVO;
import com.chipsk.im.route.cache.ServerCache;
import com.chipsk.im.route.service.AccountService;
import com.chipsk.im.route.service.CommonBizService;
import com.chipsk.im.route.service.UserInfoCacheService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;


@RestController
@Slf4j
public class RouteController implements RouteApi {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ServerCache serverCache;

    @Autowired
    private RouteHandle routeHandle;

    @Autowired
    private CommonBizService commonBizService;

    @Autowired
    private UserInfoCacheService userInfoCacheService;

    @ApiOperation("群聊API")
    @PostMapping("/groupRoute")
    @Override
    public BaseResponse<NULLBody> groupRoute(@RequestBody ChatReqVo groupReqVO) throws Exception {
        log.info("msg=[{}]", groupReqVO.toString());
        //获取所有的推送列表
        Map<Long, IMServerResVO> serverResVOMap = accountService.loadRouteRelated();
        for (Map.Entry<Long, IMServerResVO> imServerResVOEntry : serverResVOMap.entrySet()) {
            Long userId = imServerResVOEntry.getKey();
            IMServerResVO imServerResVO = imServerResVOEntry.getValue();

            //过滤掉自己
            if (userId.equals(groupReqVO.getUserId())) {
                IMUserInfo imUserInfo = userInfoCacheService.loadUserInfoByUserId(groupReqVO.getUserId());
                log.warn("过滤掉了发送者 userId={}", imUserInfo.toString());
                continue;
            }

            //推送消息
            ChatReqVo chatReqVo = new ChatReqVo(userId, groupReqVO.getMsg());
            accountService.pushMsg(imServerResVO, groupReqVO.getUserId(), chatReqVo);
        }

        return BaseResponse.okResult();
    }

    @ApiOperation("私聊API")
    @PostMapping("/p2pRoute")
    @Override
    public BaseResponse<NULLBody> p2pRoute(@RequestBody P2PReqVO p2pRequest) throws Exception {
        BaseResponse<NULLBody> res = new BaseResponse();

        try {
            //获取接收消息用户的路由信息
            IMServerResVO imServerResVO = accountService.loadRouteRelatedByUserId(p2pRequest.getReceiveUserId());

            //p2pRequest.getReceiveUserId()==>消息接收者的 userID
            ChatReqVo chatVO = new ChatReqVo(p2pRequest.getReceiveUserId(),p2pRequest.getMsg()) ;
            accountService.pushMsg(imServerResVO ,p2pRequest.getUserId(),chatVO);

            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());

        }catch (IMException e){
            res.setCode(e.getErrorCode());
            res.setMessage(e.getErrorMessage());
        }
        return res;
    }

    @ApiOperation("客户端下线")
    @PostMapping("/offLine")
    @Override
    public BaseResponse<NULLBody> offLine(@RequestBody ChatReqVo groupReqVO) throws Exception {
        IMUserInfo userInfo = userInfoCacheService.loadUserInfoByUserId(groupReqVO.getUserId());
        log.info("user [{}] offline", userInfo.toString());
        accountService.offLine(groupReqVO.getUserId());
        return BaseResponse.okResult();
    }

    @ApiOperation("登录并获取服务器")
    @PostMapping("/login")
    @Override
    public BaseResponse<IMServerResVO> login(@RequestBody LoginReqVO loginReqVO) throws Exception {
        // check server available 检测路由策略等
        String server = routeHandle.routeServer(serverCache.getServerList(),String.valueOf(loginReqVO.getUserId()));
        log.info("userName=[{}] route server info=[{}]", loginReqVO.getUserName(), server);

        // routeInfoParse
        RouteInfo routeInfo = RouteInfoParseUtil.parse(server);
        commonBizService.checkServerAvailable(routeInfo);

        //登录校验
        StatusEnum status = accountService.login(loginReqVO);
        IMServerResVO vo = null;
        if (status == StatusEnum.SUCCESS) {

            //保存路由信息
            accountService.saveRouteInfo(loginReqVO, server);
            vo = new IMServerResVO(routeInfo);
        }
        return BaseResponse.okResult(vo);
    }

    @ApiOperation("注册账号")
    @PostMapping("/registerAccount")
    @Override
    public BaseResponse<RegisterInfoResVO> registerAccount(@RequestBody RegisterInfoReqVO registerInfoReqVO) throws Exception {
        long userId = System.currentTimeMillis();
        RegisterInfoResVO info = new RegisterInfoResVO(userId, registerInfoReqVO.getUserName());
        info = accountService.register(info);
        return BaseResponse.okResult(info);
    }

    /**
     * 获取所有在线用户
     *
     * @return
     */
    @ApiOperation("获取所有在线用户")
    @PostMapping("/onlineUser")
    @Override
    public BaseResponse<Set<IMUserInfo>> onlineUser() throws Exception {
        Set<IMUserInfo> imUserInfos = userInfoCacheService.onlineUser();
        return BaseResponse.okResult(imUserInfos);
    }

}
