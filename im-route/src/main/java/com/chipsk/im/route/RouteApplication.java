package com.chipsk.im.route;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class RouteApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class, args);
        log.info("start im route success");
    }

    @Override
    public void run(String... strings) throws Exception {

    }
}
