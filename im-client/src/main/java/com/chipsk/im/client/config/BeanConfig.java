package com.chipsk.im.client.config;

import com.chipsk.im.client.handle.MsgHandleCaller;
import com.chipsk.im.client.service.impl.MsgCallBackListener;
import com.chipsk.im.common.constant.Constants;
import com.chipsk.im.common.data.construct.RingBufferWheel;
import com.chipsk.im.common.protocol.IMRequestProto;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
@Slf4j
public class BeanConfig {

    @Value("${im.user.id}")
    private long userId;

    @Value("${im.callback.thread.queue.size}")
    private int queueSize;

    @Value("${im.callback.thread.pool.size}")
    private int poolSize;


    /**
     * http client
     * @return okHttp
     */
    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        return builder.build();
    }

    /**
     * 创建心跳单例
     * @return
     */
    @Bean(value = "heartBeat")
    public IMRequestProto.IMReqProtocol heartBeat() {
        IMRequestProto.IMReqProtocol heart = IMRequestProto.IMReqProtocol.newBuilder()
                .setRequestId(userId)
                .setReqMsg("ping")
                .setType(Constants.CommandType.PING)
                .build();
        return heart;
    }

    /**
     * 创建回调线程池
     * @return
     */
    @Bean("callBackThreadPool")
    public ThreadPoolExecutor buildCallerThread(){
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue(queueSize);
        ThreadFactory product = new ThreadFactoryBuilder()
                .setNameFormat("msg-callback-%d")
                .setDaemon(true)
                .build();
        ThreadPoolExecutor productExecutor = new ThreadPoolExecutor(poolSize, poolSize, 1, TimeUnit.MILLISECONDS, queue,product);
        return  productExecutor ;
    }


    /**
     * 回调 bean
     * @return
     */
    @Bean
    public MsgHandleCaller buildCaller(){
        MsgHandleCaller caller = new MsgHandleCaller(new MsgCallBackListener()) ;
        return caller ;
    }


    @Bean
    public RingBufferWheel bufferWheel(){
        ExecutorService executorService = Executors.newFixedThreadPool(2) ;
        return new RingBufferWheel(executorService) ;
    }
}
