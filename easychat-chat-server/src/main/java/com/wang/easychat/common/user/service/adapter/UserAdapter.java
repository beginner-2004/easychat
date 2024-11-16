package com.wang.easychat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.wang.easychat.common.common.domain.enums.YesOrNoEnum;
import com.wang.easychat.common.user.domain.entity.ItemConfig;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.domain.entity.UserBackpack;
import com.wang.easychat.common.user.domain.vo.resp.BadgeResp;
import com.wang.easychat.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    public static List<BadgeResp> buidBadgeResp(List<ItemConfig> itemConfigs, List<UserBackpack> backpacks, User user) {
        Set<Long> obtainItemSet = backpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return itemConfigs.stream().map(item ->{
            BadgeResp resp = new BadgeResp();
            BeanUtil.copyProperties(item, resp);
            resp.setObtain(obtainItemSet.contains(item.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
            resp.setWearing(Objects.equals(item.getId(), user.getItemId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
            return resp;
        }).sorted(Comparator.comparing(BadgeResp::getWearing, Comparator.reverseOrder())
                .thenComparing(BadgeResp::getObtain, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
