package com.chipsk.im.client.service.impl.command;

import com.chipsk.im.client.service.EchoService;
import com.chipsk.im.client.service.InnerCommand;
import com.chipsk.im.client.service.impl.ClientInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EchoInfoCommand implements InnerCommand {


    @Autowired
    private ClientInfo clientInfo;

    @Autowired
    private EchoService echoService ;

    @Override
    public void process(String msg) {
        echoService.echo("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        echoService.echo("client info={}", clientInfo.get().getUserName());
        echoService.echo("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}
