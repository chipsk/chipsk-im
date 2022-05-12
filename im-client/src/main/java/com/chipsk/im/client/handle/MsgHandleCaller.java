package com.chipsk.im.client.handle;

import com.chipsk.im.client.service.CustomMsgHandleListener;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 消息回调bean
 */
@Data
@AllArgsConstructor
public class MsgHandleCaller {

    /**
     * 回调接口
     */
    private CustomMsgHandleListener msgHandleListener ;

}
