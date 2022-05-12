package com.chipsk.im.server;

import com.chipsk.im.server.config.AppConfiguration;
import com.chipsk.im.server.utils.RegisterZK;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;

@SpringBootApplication
@Slf4j
public class IMServerApplication implements CommandLineRunner {

    @Autowired
    private AppConfiguration appConfiguration;

    public static void main(String[] args) {
        SpringApplication.run(IMServerApplication.class, args);
        log.info("Start im server success!!!");
    }

    @Value("${server.port}")
    private int httpPort ;

    @Override
    public void run(String... strings) throws Exception {
        String address = InetAddress.getLocalHost().getHostAddress();
        Thread thread = new Thread(new RegisterZK(address, appConfiguration.getImServerPort(), httpPort));
        thread.setName("register-zk");
        thread.start();
    }
}
