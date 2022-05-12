package com.chipsk.im.client.vo.req;

import com.chipsk.im.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
public class P2PReqVO extends BaseRequest {

    @NotNull(message = "userId 不能为空")
    @ApiModelProperty(required = true, value = "消息发送者的 userId", example = "1545574049323")
    private Long userId ;


    @NotNull(message = "userId 不能为空")
    @ApiModelProperty(required = true, value = "消息接收者的 userId", example = "1545574049323")
    private Long receiveUserId ;

    @NotNull(message = "msg 不能为空")
    @ApiModelProperty(required = true, value = "msg", example = "hello")
    private String msg ;

    public P2PReqVO() {

    }

    public Long getReceiveUserId() {
        return receiveUserId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setReceiveUserId(Long receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "P2PReqVO{" +
                "userId=" + userId +
                ", receiveUserId=" + receiveUserId +
                ", msg='" + msg + '\'' +
                '}';
    }
}
