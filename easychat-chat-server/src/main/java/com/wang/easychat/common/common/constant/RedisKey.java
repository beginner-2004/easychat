package com.wang.easychat.common.common.constant;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/12
 **/
public class RedisKey {
    private static final String BASE_KEY = "easychat:chat:";

    /**
     * 用户token的key
     */
    public static final String USER_TOKEN_STRING = "userToken:uid_%d";

    /**
     * 用户的code
     */
    public static final String WAIT_LOGIN_USER_CODE = "waitUserCode:uid_%d";

    /**
     * 用户信息
     */
    public static final String USER_INTO_STRING = "userInfo:uid_%d";

    /**
     * 用户的信息更新时间
     */
    public static final String USER_MODIFY_STRING = "userModify:uid_%d";

    /**
     * 用户的信息汇总
     */
    public static final String USER_SUMMARY_STRING = "userSummary:uid_%d";

    /**
     * 房间详情
     */
    public static final String ROOM_INFO_STRING = "roomInfo:roomId_%d";

    /**
     * 群组详情
     */
    public static final String GROUP_INFO_STRING = "groupInfo:roomId_%d";

    /**
     * 热门房间列表
     */
    public static final String HOT_ROOM_ZET = "hotRoom";

    /**
     * 在线用户列表
     */
    public static final String ONLINE_UID_ZET = "online";



    public static String getKey(String key, Object... o){
        return BASE_KEY + String.format(key, o);
    }

    public static String getAllKey(){
        return BASE_KEY + "*";
    }
}
