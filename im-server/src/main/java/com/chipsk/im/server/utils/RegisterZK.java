package com.chipsk.im.server.utils;


import com.chipsk.im.server.config.AppConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegisterZK implements Runnable{

    private final ZKit zKit;

    private final AppConfiguration appConfiguration;

    private final String ip;

    private final int imServerPort;

    private final int httpPort;

    public RegisterZK(String ip, int imServerPort, int httpPort) {
        this.ip = ip;
        this.imServerPort = imServerPort;
        this.httpPort = httpPort;
        zKit = SpringBeanFactory.getBean(ZKit.class);
        appConfiguration = SpringBeanFactory.getBean(AppConfiguration.class);
    }

    @Override
    public void run() {
        //创建父节点
        zKit.createRootNode();

        //是否要将自己注册到zookeeper
        if (appConfiguration.isZkSwitch()) {
            String path = appConfiguration.getZkRoot() + "/ip-" + ip + ":" + imServerPort + ":" + httpPort;
            zKit.createNode(path);
            log.info("Registry zookeeper success, msg=[{}]", path);
        }
    }
}
