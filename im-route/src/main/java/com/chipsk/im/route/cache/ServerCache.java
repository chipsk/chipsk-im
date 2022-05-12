package com.chipsk.im.route.cache;


import com.chipsk.im.route.kit.ZKit;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ServerCache {

    @Autowired
    private LoadingCache<String, String> cache;

    @Autowired
    private ZKit zkUtil;

    public void addCache(String key) {
        cache.put(key, key);
    }

    /**
     * 获取所有的服务列表
     * @return
     */
    public List<String> getServerList() {
        List<String> list = new ArrayList<>();
        if (cache.size() == 0) {
            List<String> allNode = zkUtil.getAllNode();
            for (String node : allNode) {
                String key = node.split("-")[1];
                addCache(key);
            }
        }
        for (Map.Entry<String, String> entry : cache.asMap().entrySet()) {
            list.add(entry.getKey());
        }
        return list;
    }

    /**
     * 更新所有缓存 /先删除再添加
     * @param currentChildren
     */
    public void updateCache(List<String> currentChildren) {
        cache.invalidateAll();
        for (String currentChild : currentChildren) {
            // currentChildren = ip-127.0.0.1:11212:9082 or 127.0.0.1:11212:9082
            String key;
            if (currentChild.split("-").length == 2) {
                key = currentChild.split("-")[1];
            } else {
                key = currentChild;
            }
            addCache(key);
        }
    }

    /**
     * rebuild cache list
     */
    public void rebuildCacheList(){
        updateCache(getServerList()) ;
    }

}
