package com.chipsk.im.client.vo.req;
import com.chipsk.im.common.req.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@ToString
public class LoginReqVO extends BaseRequest {

    private Long userId ;

    private String userName ;

}
