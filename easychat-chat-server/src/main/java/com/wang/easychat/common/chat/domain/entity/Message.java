package com.wang.easychat.common.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.wang.easychat.common.chat.domain.entity.msg.MessageExtra;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 消息表
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "message", autoResultMap = true)
@ApiModel(value="Message对象", description="消息表")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "会话表id")
    private Long roomId;

    @ApiModelProperty(value = "消息发送者uid")
    private Long fromUid;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "回复的消息内容")
    private Long replyMsgId;

    @ApiModelProperty(value = "消息状态 0正常 1删除")
    /**
     * @see com.wang.easychat.common.chat.domain.enums.MessageStatusEnum
     */
    private Integer status;

    @ApiModelProperty(value = "与回复的消息间隔多少条")
    private Integer gapCount;

    @ApiModelProperty(value = "消息类型 1正常文本 2.撤回消息")
    private Integer type;

    @ApiModelProperty(value = "扩展信息")
    @TableField(value = "extra", typeHandler = JacksonTypeHandler.class)
    private MessageExtra extra;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;


}
