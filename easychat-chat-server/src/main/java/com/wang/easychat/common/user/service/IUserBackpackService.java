package com.wang.easychat.common.user.service;

import com.wang.easychat.common.user.domain.entity.UserBackpack;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-14
 */
public interface IUserBackpackService extends IService<UserBackpack> {

    Integer getCountByValidItemId(Long uid, Long id);

    UserBackpack getFirstValidItem(Long uid, Long id);

    boolean useItem(UserBackpack modifyNameItem);
}
