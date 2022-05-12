package com.chipsk.im.client.vo.req;

import com.chipsk.im.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class GoogleProtocolVO extends BaseRequest {

    @NotNull(message = "requestId 不能为空")
    @ApiModelProperty(required = true, value = "requestId", example = "123")
    private Integer requestId ;

    @NotNull(message = "msg 不能为空")
    @ApiModelProperty(required = true, value = "msg", example = "hello")
    private String msg ;

}
