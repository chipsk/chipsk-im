package com.chipsk.im.route.api.vo.req;

import com.chipsk.im.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@ToString
@Data
public class RegisterInfoReqVO extends BaseRequest {

    @NotNull(message = "用户名不能为空")
    @ApiModelProperty(required = true, value = "userName", example = "zhangsan")
    private String userName ;

}
