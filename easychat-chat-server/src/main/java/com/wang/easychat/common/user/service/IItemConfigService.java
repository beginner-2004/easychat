package com.wang.easychat.common.user.service;

import com.wang.easychat.common.user.domain.entity.ItemConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 功能物品配置表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-14
 */
public interface IItemConfigService extends IService<ItemConfig> {

    List<ItemConfig> getByType(Integer itemType);
}
