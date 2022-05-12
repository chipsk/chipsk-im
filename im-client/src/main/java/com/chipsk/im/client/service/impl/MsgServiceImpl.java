package com.chipsk.im.client.service.impl;

import com.chipsk.im.client.client.IMClient;
import com.chipsk.im.client.config.AppConfiguration;
import com.chipsk.im.client.service.InnerCommand;
import com.chipsk.im.client.service.MsgLogService;
import com.chipsk.im.client.service.MsgService;
import com.chipsk.im.client.service.RouteReqService;
import com.chipsk.im.client.service.impl.command.InnerCommandContext;
import com.chipsk.im.client.vo.req.GroupReqVO;
import com.chipsk.im.client.vo.req.P2PReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MsgServiceImpl implements MsgService, Serializable {

    @Autowired
    private IMClient imClient;


    @Resource(name = "callBackThreadPool")
    ThreadPoolExecutor executor;

    @Autowired
    private MsgLogService msgLogService;

    @Autowired
    private AppConfiguration configuration;


    @Autowired
    private RouteReqService routeReqService;

    @Autowired
    private InnerCommandContext innerCommandContext;

    private boolean aiModel = false;

    @Override
    public void sendMsg(String msg) {
        if (aiModel) {
            aiChat(msg);
        } else {
            normalChat(msg);
        }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  }

    private void normalChat(String msg) {
        String[] totalMsg = msg.split(";;");
        if (totalMsg.length > 1) {
            //私聊
            P2PReqVO p2PReqVO = new P2PReqVO();
            p2PReqVO.setUserId(configuration.getUserId());
            p2PReqVO.setReceiveUserId(Long.parseLong(totalMsg[0]));
            p2PReqVO.setMsg(totalMsg[1]);
            try {
                p2pChat(p2PReqVO);
            } catch (Exception e) {
                log.error("Exception", e);
            }

        } else {
            //群聊
            GroupReqVO groupReqVO = new GroupReqVO(configuration.getUserId(), msg);
            try {
                groupChat(groupReqVO);
            } catch (Exception e) {
                log.error("Exception", e);
            }
        }
    }

    /**
     * AI model
     *
     * @param msg
     */
    private void aiChat(String msg) {

    }

    @Override
    public void groupChat(GroupReqVO groupReqVO) throws Exception {
        routeReqService.sendGroupMsg(groupReqVO);
    }

    @Override
    public void p2pChat(P2PReqVO p2PReqVO) throws Exception {
        routeReqService.sendP2PMsg(p2PReqVO);
    }

    @Override
    public boolean checkMsg(String msg) {
        //这里没有用自己写的StringUtil
        if (StringUtils.isEmpty(msg)) {
            log.warn("不能发送空消息！");
            return true;
        }
        return false;
    }

    @Override
    public boolean innerCommand(String msg) {
        if (msg.startsWith(":")) {

            InnerCommand instance = innerCommandContext.getInstance(msg);
            instance.process(msg) ;

            return true;

        } else {
            return false;
        }
    }

    /**
     * 关闭系统
     */
    @Override
    public void shutdown() {
        log.info("系统关闭中...");
        // 客户端下线
        routeReqService.offLine();
        //异步日志存储关闭
        msgLogService.stop();
        //线程池关闭
        executor.shutdown();

        try {
            while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                log.info("线程池关闭中。。。。");
            }
            imClient.close();
        } catch (InterruptedException e) {
            log.error("InterruptedException", e);
        }
        System.exit(0);
    }

    @Override
    public void openAIModel() {

    }

    @Override
    public void closeAIModel() {

    }
}
