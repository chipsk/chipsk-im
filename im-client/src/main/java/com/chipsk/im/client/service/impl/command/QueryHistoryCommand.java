package com.chipsk.im.client.service.impl.command;

import com.chipsk.im.client.service.EchoService;
import com.chipsk.im.client.service.InnerCommand;
import com.chipsk.im.client.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QueryHistoryCommand implements InnerCommand {


    @Autowired
    private MsgLogService msgLogService ;

    @Autowired
    private EchoService echoService ;

    @Override
    public void process(String msg) {
        String[] split = msg.split(" ");
        if (split.length < 2){
            return;
        }
        String res = msgLogService.query(split[1]);
        echoService.echo(res);
    }
}
