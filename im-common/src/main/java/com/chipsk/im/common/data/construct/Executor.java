package com.chipsk.im.common.data.construct;

import com.chipsk.im.common.pojo.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class Executor extends Thread {


    private BlockingQueue<TimerTask> queue = new LinkedBlockingQueue<>();

    public void execute(TimerTask task) {
        try {
            if (task != null) {
                queue.put(task);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("阻塞队列put中断异常", e);
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {

            TimerTask task = null;
            try {
                task = queue.take();
            } catch (InterruptedException e) {
                return;
            }

            try {
                task.run();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public Set<TimerTask> getUnprocessedTasks() {
        return new HashSet<>(queue);
    }
}