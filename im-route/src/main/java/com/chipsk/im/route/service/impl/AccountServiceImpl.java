package com.chipsk.im.route.service.impl;

import com.chipsk.im.common.enums.StatusEnum;
import com.chipsk.im.common.exception.IMException;
import com.chipsk.im.common.pojo.IMUserInfo;
import com.chipsk.im.common.proxy.ProxyManager;
import com.chipsk.im.common.util.RouteInfoParseUtil;
import com.chipsk.im.route.api.vo.req.ChatReqVo;
import com.chipsk.im.route.api.vo.req.LoginReqVO;
import com.chipsk.im.route.api.vo.res.IMServerResVO;
import com.chipsk.im.route.api.vo.res.RegisterInfoResVO;
import com.chipsk.im.route.constant.InfoConstant;
import com.chipsk.im.route.service.AccountService;
import com.chipsk.im.route.service.UserInfoCacheService;
import com.chipsk.im.server.api.vo.ServerApi;
import com.chipsk.im.server.api.vo.req.SendMsgReqVO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private UserInfoCacheService userInfoCacheService;



    @Override
    public RegisterInfoResVO register(RegisterInfoResVO info) throws Exception {
        String key = InfoConstant.ACCOUNT_PREFIX + info.getUserId();
        // im-account:1651553404515
        String account = redisTemplate.opsForValue().get(info.getUserName());
        if (null == account) {
            //为了方便查询，冗余一份
            redisTemplate.opsForValue().set(key, info.getUserName());
            redisTemplate.opsForValue().set(info.getUserName(), key);
        } else {
            long userId = Long.parseLong(account.split(":")[1]);
            info.setUserId(userId);
            info.setUserName(info.getUserName());
        }
        return info;
    }

    @Override
    public StatusEnum login(LoginReqVO loginReqVO) throws Exception {

        //在Redis里查询
        String key = InfoConstant.ACCOUNT_PREFIX + loginReqVO.getUserId();
        String username = redisTemplate.opsForValue().get(key);

        //校验用户名
        if (null == username) {
            return StatusEnum.ACCOUNT_NOT_MATCH;
        }

        if (!username.equals(loginReqVO.getUserName())) {
            return StatusEnum.ACCOUNT_NOT_MATCH;
        }

        //登录成功, 保存登录状态
        boolean status = userInfoCacheService.saveAndCheckUserLoginStatus(loginReqVO.getUserId());
        if (!status) {
            return StatusEnum.REPEAT_LOGIN;
        }
        return StatusEnum.SUCCESS;
    }

    @Override
    public void saveRouteInfo(LoginReqVO loginReqVO, String msg) throws Exception {
        String key = InfoConstant.ROUTE_PREFIX + loginReqVO.getUserId();
        //存入redis
        redisTemplate.opsForValue().set(key, msg);
    }

    @Override
    public Map<Long, IMServerResVO> loadRouteRelated() {
        Map<Long, IMServerResVO> routes = new HashMap<>(64);

        /*
          TODO 待研究 由于 Redis 单线程的特质，当数据量大时；一旦使用 keys 匹配所有 cim-route:* 数据，会导致 Redis 不能处理其他请求
          所以这里改为使用 scan 命令来遍历所有的 cim-route:*。
         */
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        ScanOptions options = ScanOptions.scanOptions()
                .match(InfoConstant.ROUTE_PREFIX + "*")
                .build();
        Cursor<byte[]> scan = connection.scan(options);

        while (scan.hasNext()) {
            byte[] next = scan.next();
            String key = new String(next, StandardCharsets.UTF_8);
            log.info("key={}", key);
            //解析服务信息
            parseServerInfo(routes, key);
        }
        try {
            scan.close();
        } catch (IOException e) {
            log.error("IOException", e);
        }
        return routes;
    }

    private void parseServerInfo(Map<Long, IMServerResVO> routes, String key) {
        Long userId = Long.valueOf(key.split(":")[1]);
        String value = redisTemplate.opsForValue().get(key);
        IMServerResVO imServerResVO = new IMServerResVO(RouteInfoParseUtil.parse(value));
        routes.put(userId, imServerResVO);
    }

    @Override
    public IMServerResVO loadRouteRelatedByUserId(Long userId) {
        String value = redisTemplate.opsForValue().get(InfoConstant.ROUTE_PREFIX + userId);

        if (value == null) {
            throw new IMException(StatusEnum.OFF_LINE);
        }

        IMServerResVO imServerResVO = new IMServerResVO(RouteInfoParseUtil.parse(value));
        return imServerResVO;
    }

    @Override
    public void pushMsg(IMServerResVO imServerResVO, long sendUserId, ChatReqVo groupReqVO) throws Exception {
        IMUserInfo imUserInfo = userInfoCacheService.loadUserInfoByUserId(sendUserId);

        String url = "http://" + imServerResVO.getIp() + ":" + imServerResVO.getHttpPort();
        ServerApi serverApi = new ProxyManager<>(ServerApi.class, url, okHttpClient).getInstance();
        SendMsgReqVO vo = new SendMsgReqVO(imUserInfo.getUserName() + ":" + groupReqVO.getMsg(), groupReqVO.getUserId());
        Response response = null;
        try {
            response = (Response) serverApi.sendMsg(vo);
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            response.body().close();
        }
    }

    @Override
    public void offLine(Long userId) throws Exception {

        // TODO: 2019-01-21 改为一个原子命令，以防数据一致性

        //删除路由
        redisTemplate.delete(InfoConstant.ROUTE_PREFIX + userId);

        //删除登录状态
        userInfoCacheService.removeLoginStatus(userId);
    }
}
