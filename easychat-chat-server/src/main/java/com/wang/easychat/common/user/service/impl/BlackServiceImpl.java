package com.wang.easychat.common.user.service.impl;

import com.wang.easychat.common.user.domain.entity.Black;
import com.wang.easychat.common.user.mapper.BlackMapper;
import com.wang.easychat.common.user.service.IBlackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-19
 */
@Service
public class BlackServiceImpl extends ServiceImpl<BlackMapper, Black> implements IBlackService {

}
