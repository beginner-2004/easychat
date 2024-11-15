package com.wang.easychat.common.user.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.wang.easychat.common.common.domain.enums.YesOrNoEnum;
import com.wang.easychat.common.user.domain.entity.UserBackpack;
import com.wang.easychat.common.user.mapper.UserBackpackMapper;
import com.wang.easychat.common.user.service.IUserBackpackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-14
 */
@Service
public class UserBackpackServiceImpl extends ServiceImpl<UserBackpackMapper, UserBackpack> implements IUserBackpackService {

    @Override
    public Integer getCountByValidItemId(Long uid, Long id) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, id)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .count();
    }
}
