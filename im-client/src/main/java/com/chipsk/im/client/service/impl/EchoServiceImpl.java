package com.chipsk.im.client.service.impl;

import com.chipsk.im.client.config.AppConfiguration;
import com.chipsk.im.client.service.EchoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class EchoServiceImpl implements EchoService, Serializable {

    private static final String PREFIX = "$";

    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public void echo(String msg, Object... replace) {
        String date = LocalDate.now().toString() + " " + LocalTime.now().withNano(0).toString();

        msg = "[" + date + "] \033[31;4m" + appConfiguration.getUserName() + PREFIX + "\033[0m" + " " + msg;

        String log = print(msg, replace);

        System.out.println(log);
    }


    /**
     * print msg
     * @param msg
     * @param replace
     * @return
     */
    private String print(String msg, Object... replace) {
        StringBuilder sb = new StringBuilder();
        int k = 0;
        for (int i = 0; i < replace.length; i++) {
            int index = msg.indexOf("{}", k);

            if (index == -1) {
                return msg;
            }

            if (index != 0) {
                sb.append(msg, k, index);
                sb.append(replace[i]);

                if (replace.length == 1) {
                    sb.append(msg, index + 2, msg.length());
                }
            } else {
                sb.append(replace[i]);
                if (replace.length == 1) {
                    sb.append(msg, index + 2, msg.length());
                }
            }

            k = index + 2;
        }
        if (sb.toString().equals("")) {
            return msg;
        } else {
            return sb.toString();
        }
    }


}

