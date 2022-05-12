package com.chipsk.im.client.service.impl.command;

import com.chipsk.im.client.service.EchoService;
import com.chipsk.im.client.service.InnerCommand;
import com.chipsk.im.client.service.MsgService;
import com.chipsk.im.common.data.construct.RingBufferWheel;
import com.chipsk.im.common.data.construct.TimerTask;
import com.chipsk.im.common.data.construct.ZSetTimer;
import com.chipsk.im.common.pojo.Task;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DelayMsgCommand implements InnerCommand {

    @Autowired
    private EchoService echoService;

    @Autowired
    private MsgService msgService;

    @Autowired
    private RingBufferWheel ringBufferWheel ;

    @Override
    public void process(String msg) {
        if (msg.split(" ").length <= 2) {
            echoService.echo("incorrect commond, :delay [msg] [delayTime]");
            return;
        }

        String message = msg.split(" ")[1];
        int delayTime = Integer.parseInt(msg.split(" ")[2]);
        RingBufferWheel.Task task = new DelayMsgJob(message) ;
        task.setKey(delayTime);
        ringBufferWheel.addTask(task);
        echoService.echo(EmojiParser.parseToUnicode(msg));

/*        ZSetTimer rt = new ZSetTimer();
        DelayMsgJob job = new DelayMsgJob();
        job.setMsg(message);
        rt.addTask(job, delayTime, TimeUnit.SECONDS);
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        msgService.sendMsg(msg);
        echoService.echo(EmojiParser.parseToUnicode(msg));
        rt.stop();*/
    }



    private class DelayMsgJob extends RingBufferWheel.Task{

        private String msg ;

        public DelayMsgJob(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            msgService.sendMsg(msg);
        }
    }


//    class DelayMsgJob extends TimerTask implements Serializable {
//
//        private static final long serialVersionUID = 1L;
//
//        private String id;
//
//        private String msg;
//
//        public DelayMsgJob() {
//            this(UUID.randomUUID().toString());
//        }
//
//        public DelayMsgJob(String id) {
//            this(id, "init");
//        }
//
//        public DelayMsgJob(String id, String msg) {
//            this.id = id;
//            this.msg = msg;
//        }
//
//        @Override
//        public void run() {
//            System.out.println("方法执行了");
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public String getMsg() {
//            return msg;
//        }
//
//        public void setMsg(String msg) {
//            this.msg = msg;
//        }
//
//        @Override
//        public String toString() {
//            return "DelayMsgJob{" +
//                    "id='" + id + '\'' +
//                    ", msg='" + msg + '\'' +
//                    '}';
//        }
//    }
}
