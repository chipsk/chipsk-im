package com.chipsk.im.common.proxy;


import com.alibaba.fastjson.JSONObject;
import com.chipsk.im.common.exception.IMException;
import com.chipsk.im.common.util.HttpClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.lang.reflect.*;

import static com.chipsk.im.common.enums.StatusEnum.VALIDATION_FAIL;

@Slf4j
public class ProxyManager<T> {

    private Class<T> clazz;

    private String url;

    private OkHttpClient okHttpClient;

    /**
     *
     * @param clazz Proxied interface
     * @param url server provider url
     * @param okHttpClient http client
     */
    public ProxyManager(Class<T> clazz, String url, OkHttpClient okHttpClient) {
        this.clazz = clazz;
        this.url = url;
        this.okHttpClient = okHttpClient;
    }

    public T getInstance() {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, new ProxyInvocation());
    }


    private class ProxyInvocation implements InvocationHandler {


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            JSONObject jsonObject = new JSONObject();
            // 动态代理
            String serverUrl = url + "/" + method.getName();

            //参数校验
            if (args != null && args.length > 1) {
                throw new IMException(VALIDATION_FAIL);
            }

            if (method.getParameterTypes().length > 0) {
                Object para = null;
                //非空判断
                if (args != null) {
                    para = args[0];
                }
                Class<?> parameterType = method.getParameterTypes()[0];
                for (Field field : parameterType.getDeclaredFields()) {
                    field.setAccessible(true);
                    jsonObject.put(field.getName(), field.get(para));
                }
            }
            return HttpClient.call(okHttpClient, jsonObject.toString(), serverUrl);

        }
    }


}
