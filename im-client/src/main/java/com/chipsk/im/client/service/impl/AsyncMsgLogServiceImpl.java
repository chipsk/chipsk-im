package com.chipsk.im.client.service.impl;

import com.chipsk.im.client.config.AppConfiguration;
import com.chipsk.im.client.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO 异步的日志存储操作待研究
 */
@Service
@Slf4j
public class AsyncMsgLogServiceImpl implements MsgLogService {

    @Autowired
    private AppConfiguration appConfiguration;

    /**
     * The default buffer size.
     */
    private static final int DEFAULT_QUEUE_SIZE = 16;
    private BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(DEFAULT_QUEUE_SIZE);

    private volatile boolean started = false;
    private Worker worker = new Worker();

    @Override
    public void log(String msg) {
        //开始消费
        startMsgLogger();
        try {
            // TODO: 2019/1/6 消息堆满是否阻塞线程？
            blockingQueue.put(msg);
        } catch (InterruptedException e) {
            log.error("InterruptedException", e);
        }
    }

    @Override
    public void stop() {
        started = false;
        worker.interrupt();
    }

    @Override
    public String query(String key) {
        StringBuilder sb = new StringBuilder();

        Path path = Paths.get(appConfiguration.getMsgLoggerPath() + appConfiguration.getUserName() + "/");

        try {
            Stream<Path> list = Files.list(path);
            List<Path> collect = list.collect(Collectors.toList());
            for (Path file : collect) {
                List<String> strings = Files.readAllLines(file);
                for (String msg : strings) {
                    if (msg.trim().contains(key)) {
                        sb.append(msg).append("\n");
                    }
                }

            }
        } catch (IOException e) {
            log.info("IOException", e);
        }

        return sb.toString().replace(key, "\033[31;4m" + key + "\033[0m");
    }


    private class Worker extends Thread {
        @Override
        public void run() {
            while (started) {
                try {
                    String msg = blockingQueue.take();
                    writeLog(msg);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }


    /**
     * 开始工作
     */
    private void startMsgLogger() {
        if (started) {
            return;
        }

        worker.setDaemon(true);
        worker.setName("AsyncMsgLogger-Worker");
        started = true;
        worker.start();
    }


    private void writeLog(String msg) {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();

        String dir = appConfiguration.getMsgLoggerPath() + appConfiguration.getUserName() + "/";
        String fileName = dir + year + month + day + ".log";

        Path file = Paths.get(fileName);
        boolean exists = Files.exists(Paths.get(dir), LinkOption.NOFOLLOW_LINKS);
        try {
            if (!exists) {
                Files.createDirectories(Paths.get(dir));
            }

            List<String> lines = Arrays.asList(msg);

            Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.info("IOException", e);
        }
    }
}
