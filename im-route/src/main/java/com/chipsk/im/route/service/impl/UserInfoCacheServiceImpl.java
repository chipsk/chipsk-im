package com.chipsk.im.route.service.impl;

import com.chipsk.im.common.constant.Constants;
import com.chipsk.im.common.pojo.IMUserInfo;
import com.chipsk.im.route.constant.InfoConstant;
import com.chipsk.im.route.service.UserInfoCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserInfoCacheServiceImpl implements UserInfoCacheService {


    /**
     * TODO 本地缓存，为了防止内存撑爆，后期可换为 LRU。
     */
    private final static Map<Long,IMUserInfo> USER_INFO_MAP = new ConcurrentHashMap<>(64) ;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public IMUserInfo loadUserInfoByUserId(Long userId) {

        //优先从本地缓存获取
        IMUserInfo imUserInfo = USER_INFO_MAP.get(userId);
        if (null != imUserInfo) {
            return imUserInfo;
        }

        //load redis
        String sendUserName = redisTemplate.opsForValue().get(InfoConstant.ACCOUNT_PREFIX + userId);
        if (sendUserName != null) {
            imUserInfo = new IMUserInfo(userId, sendUserName);
            USER_INFO_MAP.put(userId, imUserInfo);
        }
        return imUserInfo;
    }

    @Override
    public boolean saveAndCheckUserLoginStatus(Long userId) throws Exception {
        Long add = redisTemplate.opsForSet().add(InfoConstant.LOGIN_STATUS_PREFIX, userId.toString());
        return add != 0;
    }

    @Override
    public void removeLoginStatus(Long userId) throws Exception {
        redisTemplate.opsForSet().remove(InfoConstant.LOGIN_STATUS_PREFIX, userId.toString());
    }

    @Override
    public Set<IMUserInfo> onlineUser() {
        Set<IMUserInfo> set = null;
        Set<String> members = redisTemplate.opsForSet().members(InfoConstant.LOGIN_STATUS_PREFIX);
        System.out.println(members);
        for (String member : members) {
            if (set == null) {
                set = new HashSet<>(64);
            }
            IMUserInfo userInfo = loadUserInfoByUserId(Long.valueOf(member));
            set.add(userInfo);
        }
        return set;
    }
}
