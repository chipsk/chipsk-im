package com.chipsk.im.route.kit;


import com.alibaba.fastjson.JSON;
import com.chipsk.im.route.cache.ServerCache;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ZKit {

    @Autowired
    private ZkClient zkClient;

    @Autowired
    private ServerCache serverCache ;

    /**
     * get all server node from zookeeper
     * @return
     */
    public List<String> getAllNode(){
        List<String> children = zkClient.getChildren("/route");
        log.info("Query all node =[{}] success.", JSON.toJSONString(children));
        return children;
    }


}
