package com.chipsk.im.common.protocol;

import com.chipsk.im.common.data.construct.ZSetTimer;
import com.chipsk.im.common.req.GoogleProtocolTestVO;
import com.chipsk.im.common.util.JedisUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/8/1 12:24
 * @since JDK 1.8
 */

public class ProtocolUtil {


    public static void main(String[] args) throws IOException {

        GoogleProtocolTestVO vo = new GoogleProtocolTestVO(1, "hello");
        IMRequestProto.IMReqProtocol protocol = IMRequestProto.IMReqProtocol.newBuilder()
                .setRequestId(vo.getRequestId())
                .setReqMsg(vo.getMsg())
                .setType(3)
                .build();


        System.out.println(protocol);

//        ZSetTimer zSetTimer = new ZSetTimer();
//        zSetTimer.doAddTask(encode, 1, TimeUnit.SECONDS);
//
        Jedis jedis = JedisUtil.getJedis();
//        long score = System.currentTimeMillis();
//        jedis.zadd("queueName", score, Arrays.toString(encode));

        Set<String> queueName = jedis.zrevrange("queueName", 0, 2);
        for (String s : queueName) {
            IMRequestProto.IMReqProtocol decode = decode(s.getBytes());
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOutStream);
            out.writeObject(decode);
        }

//        byte[] bytes = queueName.getBytes();
//        IMRequestProto.IMReqProtocol decode = decode(bytes);
//        System.out.println(decode);


    }

    /**
     * 编码
     * @param protocol
     * @return
     */
    public static byte[] encode(IMRequestProto.IMReqProtocol protocol){
        return protocol.toByteArray() ;
    }

    /**
     * 解码
     * @param bytes
     * @return
     * @throws InvalidProtocolBufferException
     */
    public static IMRequestProto.IMReqProtocol decode(byte[] bytes) throws InvalidProtocolBufferException {
        return IMRequestProto.IMReqProtocol.parseFrom(bytes);
    }
}
