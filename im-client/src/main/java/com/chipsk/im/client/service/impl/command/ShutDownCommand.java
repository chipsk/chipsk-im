package com.chipsk.im.client.service.impl.command;

import com.chipsk.im.client.client.IMClient;
import com.chipsk.im.client.service.EchoService;
import com.chipsk.im.client.service.InnerCommand;
import com.chipsk.im.client.service.MsgLogService;
import com.chipsk.im.client.service.RouteReqService;
import com.chipsk.im.client.service.impl.ShutDownMsg;
import com.chipsk.im.common.data.construct.RingBufferWheel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ShutDownCommand implements InnerCommand {

    @Autowired
    private RouteReqService routeReqService ;

    @Autowired
    private IMClient imClient;

    @Autowired
    private MsgLogService msgLogService;

    @Resource(name = "callBackThreadPool")
    private ThreadPoolExecutor callBackExecutor;

    @Autowired
    private EchoService echoService ;


    @Autowired
    private ShutDownMsg shutDownMsg ;

    @Autowired
    private RingBufferWheel ringBufferWheel ;

    @Override
    public void process(String msg) {
        echoService.echo("im client closing...");
        shutDownMsg.shutdown();
        routeReqService.offLine();
        msgLogService.stop();
        callBackExecutor.shutdown();
        ringBufferWheel.stop(false);
        try {
            while (!callBackExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                echoService.echo("thread pool closing");
            }
            imClient.close();
        } catch (InterruptedException e) {
            log.error("InterruptedException", e);
        }
        echoService.echo("cim close success!");
        System.exit(0);
    }
}
