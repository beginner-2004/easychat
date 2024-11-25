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
 * 房间表
 * </p>
 *
 * @author wang
 * @since 2024-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("room")
@ApiModel(value="Room对象", description="房间表")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "房间类型 1群聊 2单聊")
    private Integer type;

    @ApiModelProperty(value = "是否全员展示 0否 1是")
    private Integer hotFlag;

    @ApiModelProperty(value = "群最后消息的更新时间（热点群不需要写扩散，只更新这里）")
    private LocalDateTime activeTime;

    @ApiModelProperty(value = "会话中的最后一条消息id")
    private Long lastMsgId;

    @ApiModelProperty(value = "额外信息（根据不同类型房间有不同存储的东西）")
    private String extJson;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;


}
