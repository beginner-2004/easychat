package com.wang.easychat.common.user.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 单聊房间表
 * </p>
 *
 * @author wang
 * @since 2024-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("room_friend")
@ApiModel(value="RoomFriend对象", description="单聊房间表")
public class RoomFriend implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "房间id")
    private Long roomId;

    @ApiModelProperty(value = "uid1（更小的uid）")
    private Long uid1;

    @ApiModelProperty(value = "uid2（更大的uid）")
    private Long uid2;

    @ApiModelProperty(value = "房间key由两个uid拼接，先做排序uid1_uid2")
    private String roomKey;

    @ApiModelProperty(value = "房间状态 0正常 1禁用(删好友了禁用)")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;


}
