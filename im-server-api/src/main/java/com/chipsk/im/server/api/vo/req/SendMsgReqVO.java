package com.chipsk.im.server.api.vo.req;


import com.chipsk.im.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SendMsgReqVO extends BaseRequest {

    @NotNull(message = "msg 不能为空")
    @ApiModelProperty(required = true, value = "msg", example = "hello")
    private String msg ;

    @NotNull(message = "userId 不能为空")
    @ApiModelProperty(required = true, value = "userId", example = "11")
    private Long userId ;

}
