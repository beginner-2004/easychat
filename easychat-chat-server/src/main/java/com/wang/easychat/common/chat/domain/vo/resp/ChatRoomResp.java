package com.wang.easychat.common.chat.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassDescription: 房间消息预览展示体
 * @Author:Wangzd
 * @Date: 2024/12/10
 **/
@Data
public class ChatRoomResp {
    @ApiModelProperty("房间号")
    private Long roomId;
    @ApiModelProperty("房间类型 1群聊 2单聊")
    private Integer type;
    @ApiModelProperty("是否全员展示的会话 0否 1是")
    private Integer hot_Flag;
    @ApiModelProperty("最新消息")
    private String text;
    @ApiModelProperty("会话名称")
    private String name;
    @ApiModelProperty("会话头像")
    private String avatar;
    @ApiModelProperty("房间最后活跃时间(用来排序)")
    private Date activeTime;
    @ApiModelProperty("未读数")
    private Integer unreadCount;

}
