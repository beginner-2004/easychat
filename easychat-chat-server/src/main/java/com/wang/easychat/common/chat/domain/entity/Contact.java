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
 * 会话列表
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("contact")
@ApiModel(value="Contact对象", description="会话列表")
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "uid")
    private Long uid;

    @ApiModelProperty(value = "房间id")
    private Long roomId;

    @ApiModelProperty(value = "阅读到的时间")
    private LocalDateTime readTime;

    @ApiModelProperty(value = "会话内消息最后更新的时间(只有普通会话需要维护，全员会话不需要维护)")
    private LocalDateTime activeTime;

    @ApiModelProperty(value = "会话最新消息id")
    private Long lastMsgId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;


}
