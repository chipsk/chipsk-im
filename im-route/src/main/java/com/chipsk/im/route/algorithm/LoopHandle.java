package com.chipsk.im.route.algorithm;

import com.chipsk.im.common.enums.StatusEnum;
import com.chipsk.im.common.exception.IMException;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 轮询路由算法
 */
public class LoopHandle implements RouteHandle{

    private AtomicLong index = new AtomicLong();

    @Override
    public String routeServer(List<String> values, String key) {
        if (values.size() == 0) {
            throw new IMException(StatusEnum.SERVER_NOT_AVAILABLE);
        }
        Long position = index.incrementAndGet() % values.size();
        if (position < 0) {
            position = 0L;
        }

        return values.get(position.intValue());
    }
}
