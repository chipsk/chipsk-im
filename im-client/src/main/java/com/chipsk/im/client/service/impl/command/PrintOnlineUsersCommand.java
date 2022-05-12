package com.chipsk.im.client.service.impl.command;

import com.chipsk.im.client.service.EchoService;
import com.chipsk.im.client.service.InnerCommand;
import com.chipsk.im.client.service.RouteReqService;
import com.chipsk.im.client.vo.res.OnlineUsersResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PrintOnlineUsersCommand implements InnerCommand {


    @Autowired
    private RouteReqService routeReqService ;

    @Autowired
    private EchoService echoService ;

    @Override
    public void process(String msg) {
        try {
            List<OnlineUsersResVO.DataBodyBean> onlineUsers = routeReqService.onlineUsers();

            echoService.echo("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            for (OnlineUsersResVO.DataBodyBean onlineUser : onlineUsers) {
                echoService.echo("userId={}=====userName={}",onlineUser.getUserId(),onlineUser.getUserName());
            }
            echoService.echo("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        } catch (Exception e) {
            log.error("Exception", e);
        }
    }
}