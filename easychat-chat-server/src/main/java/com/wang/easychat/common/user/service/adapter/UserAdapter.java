package com.wang.easychat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/11
 **/
public class UserAdapter {
    public static User buildUserSave(String openId) {
        return User.builder().openId(openId).build();
    }

    public static User buildAuthorizeUser(Long id, WxOAuth2UserInfo userInfo) {
        User user = User.builder()
                .id(id)
                .avatar(userInfo.getHeadImgUrl())
                .name(userInfo.getNickname())
                .sex(userInfo.getSex())
                .build();
        if (userInfo.getNickname().length() > 6) {
            user.setName("名字过长" + RandomUtil.randomInt(100000));
        } else {
            user.setName(userInfo.getNickname());
        }
        return user;
    }

    public static UserInfoResp buildUserInfo(User user, Integer modifyNameCount) {
        UserInfoResp vo = new UserInfoResp();
        BeanUtil.copyProperties(user, vo);
        vo.setId(user.getId());
        vo.setModifyNameChance(modifyNameCount);
        return vo;
    }
}
