package com.chipsk.im.client.thread;

import com.chipsk.im.client.config.AppConfiguration;
import com.chipsk.im.client.service.EchoService;
import com.chipsk.im.client.service.MsgLogService;
import com.chipsk.im.client.service.MsgService;
import com.chipsk.im.client.utils.SpringBeanFactory;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class Scan implements Runnable{

    /**
     * 系统参数
     */
    private final AppConfiguration appConfiguration;

    private final MsgLogService msgLogService;

    private final MsgService msgService;

    private final EchoService echoService;

    public Scan() {
        this.appConfiguration = SpringBeanFactory.getBean(AppConfiguration.class);
        this.msgService = SpringBeanFactory.getBean(MsgService.class);
        this.msgLogService = SpringBeanFactory.getBean(MsgLogService.class);
        this.echoService = SpringBeanFactory.getBean(EchoService.class);
    }
    /**
     * 输入消息处理
     */
    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String msg = sc.nextLine();

            // 检查消息
            if (msgService.checkMsg(msg)) {
                continue;
            }

            //TODO 系统内置命令
            if (msgService.innerCommand(msg)) {
                continue;
            }

            // 发送消息
            msgService.sendMsg(msg);

            //写入聊天记录
            msgLogService.log(msg);

            //输出消息到Terminal
            echoService.echo(EmojiParser.parseToUnicode(msg));
        }
    }
}
