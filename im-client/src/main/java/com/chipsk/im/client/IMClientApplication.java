package com.chipsk.im.client;


import com.chipsk.im.client.service.impl.ClientInfo;
import com.chipsk.im.client.thread.Scan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class IMClientApplication implements CommandLineRunner {

    @Autowired
    private ClientInfo clientInfo ;

    public static void main(String[] args) {
        SpringApplication.run(IMClientApplication.class, args);
        log.info("启动client服务成功");
    }

    @Override
    public void run(String... strings) throws Exception {
        Scan scan = new Scan();
        Thread thread = new Thread(scan);
        thread.setName("scan-thread");
        thread.start();
        clientInfo.saveStartDate();
    }
}
