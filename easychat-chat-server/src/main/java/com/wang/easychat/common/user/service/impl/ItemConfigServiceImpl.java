package com.wang.easychat.common.user.service.impl;

import com.wang.easychat.common.user.domain.entity.ItemConfig;
import com.wang.easychat.common.user.mapper.ItemConfigMapper;
import com.wang.easychat.common.user.service.IItemConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 功能物品配置表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-14
 */
@Service
public class ItemConfigServiceImpl extends ServiceImpl<ItemConfigMapper, ItemConfig> implements IItemConfigService {

    @Override
    public List<ItemConfig> getByType(Integer itemType) {
        return lambdaQuery()
                .eq(ItemConfig::getType, itemType)
                .list();
    }
}
