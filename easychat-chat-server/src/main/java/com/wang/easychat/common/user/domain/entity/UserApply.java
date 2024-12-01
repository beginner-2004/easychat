package com.wang.easychat.common.user.domain.entity;

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
 * 用户申请表
 * </p>
 *
 * @author wang
 * @since 2024-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_apply")
@ApiModel(value="UserApply对象", description="用户申请表")
public class UserApply implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "申请人uid")
    private Long uid;

    @ApiModelProperty(value = "申请类型 1加好友")
    private Integer type;

    @ApiModelProperty(value = "接收人uid")
    private Long targetId;

    @ApiModelProperty(value = "申请信息")
    private String msg;

    @ApiModelProperty(value = "申请状态 1待审批 2同意")
    private Integer status;

    @ApiModelProperty(value = "阅读状态 1未读 2已读")
    private Integer readStatus;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;


}
