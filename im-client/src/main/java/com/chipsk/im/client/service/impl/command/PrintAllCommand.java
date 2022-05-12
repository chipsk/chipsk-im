package com.chipsk.im.client.service.impl.command;

import com.chipsk.im.client.service.EchoService;
import com.chipsk.im.client.service.InnerCommand;
import com.chipsk.im.common.enums.SystemCommandEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class PrintAllCommand implements InnerCommand {

    @Autowired
    private EchoService echoService ;

    @Override
    public void process(String msg) {
        Map<String, String> allStatusCode = SystemCommandEnum.getAllStatusCode();
        echoService.echo("====================================");
        for (Map.Entry<String, String> stringStringEntry : allStatusCode.entrySet()) {
            String key = stringStringEntry.getKey();
            String value = stringStringEntry.getValue();
            echoService.echo(key + "----->" + value);
        }
        echoService.echo("====================================");
    }
}
