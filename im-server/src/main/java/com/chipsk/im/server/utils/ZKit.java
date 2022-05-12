package com.chipsk.im.server.utils;


import com.chipsk.im.server.config.AppConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * zookeeper工具
 */
@Slf4j
@Component
public class ZKit {

    //这里用autowired会报错
    @Autowired
    private ZkClient zkClient;

    @Autowired
    private AppConfiguration appConfiguration;


    public void createRootNode() {
        boolean exist = zkClient.exists(appConfiguration.getZkRoot());
        if (exist) {
            return;
        }
        //创建根节点
        zkClient.createPersistent(appConfiguration.getZkRoot());
    }

    public void createNode(String path) {
        zkClient.createEphemeral(path);
    }

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2182", 2000);
        AppConfiguration appConfiguration = new AppConfiguration();
        System.out.println("是否存在:" + zkClient.exists(appConfiguration.getZkRoot()));
    }

}
