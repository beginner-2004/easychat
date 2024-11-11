package com.wang.easychat.common.user.mapper;

import com.wang.easychat.common.user.domain.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author wang
 * @since 2024-11-08
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
