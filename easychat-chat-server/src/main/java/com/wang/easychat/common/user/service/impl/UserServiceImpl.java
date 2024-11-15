package com.wang.easychat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.domain.enums.ItemEnum;
import com.wang.easychat.common.user.domain.vo.resp.UserInfoResp;
import com.wang.easychat.common.user.mapper.UserMapper;
import com.wang.easychat.common.user.service.IUserBackpackService;
import com.wang.easychat.common.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.easychat.common.user.service.adapter.UserAdapter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private IUserBackpackService userBackpackService;

    public User getByOpenId(String openId) {
        return lambdaQuery()
                .eq(User::getOpenId, openId)
                .one();
    }

    @Override
    @Transactional
    public Long register(User insert) {
        save(insert);
        // todo 用户注册事件
        return insert.getId();

    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = getById(uid);
        Integer modifyNameCount = userBackpackService.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfo(user, modifyNameCount);
    }

    @Override
    public void modifyName(Long uid, String name) {
        if (StrUtil.isBlank(name)){

        }
        if (name.length() > 6){

        }
    }
}

