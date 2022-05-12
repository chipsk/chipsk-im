package com.chipsk.im.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Info {

    private String userName;

    private long userId ;

    private String serviceInfo ;

    private Date startDate ;

}
