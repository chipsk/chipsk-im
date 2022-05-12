package com.chipsk.im.route.api.vo.req;

import com.chipsk.im.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * Google Protocol 编解码发送
 */
public class ChatReqVo extends BaseRequest {

    @NotNull(message = "userId 不能为空")
    @ApiModelProperty(required = true, value = "userId", example = "1545574049323")
    private Long userId ;

    @NotNull(message = "msg 不能为空")
    @ApiModelProperty(required = true, value = "msg", example = "hello")
    private String msg ;

    public ChatReqVo() {

    }

    public ChatReqVo(Long userId, String msg) {
        this.userId = userId;
        this.msg = msg;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ChatReqVo{" +
                "userId=" + userId +
                ", msg='" + msg + '\'' +
                '}';
    }
}
