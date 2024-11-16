package com.wang.easychat.common.user.service;

import com.wang.easychat.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.easychat.common.user.domain.vo.resp.BadgeResp;
import com.wang.easychat.common.user.domain.vo.resp.UserInfoResp;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-08
 */
@Service
public interface IUserService extends IService<User> {
    User getByOpenId(String openId);

    Long register(User insert);

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid, String name);

    List<BadgeResp> badges(Long uid);

    void wearingBadge(Long uid, Long itemId);
}
