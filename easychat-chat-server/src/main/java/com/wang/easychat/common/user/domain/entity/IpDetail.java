package com.wang.easychat.common.user.domain.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/18
 **/
@Data
public class IpDetail implements Serializable {

    private String ip;
    private String isp;
    private String isp_id;
    private String city;
    private String city_id;
    private String county;
    private String country_id;
    private String region;
    private String region_id;

}
