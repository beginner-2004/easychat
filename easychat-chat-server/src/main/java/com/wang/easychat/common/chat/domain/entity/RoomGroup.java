package com.wang.easychat.common.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 群聊房间表
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("room_group")
@ApiModel(value="RoomGroup对象", description="群聊房间表")
public class RoomGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "房间id")
    private Long roomId;

    @ApiModelProperty(value = "群名称")
    private String name;

    @ApiModelProperty(value = "群头像")
    private String avatar;

    @ApiModelProperty(value = "额外信息（根据不同类型房间有不同存储的东西）")
    private String extJson;

    @ApiModelProperty(value = "逻辑删除(0-正常,1-删除)")
    private Integer deleteStatus;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;


}
