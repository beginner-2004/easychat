package com.wang.easychat.common.user.service.cache;

import com.wang.easychat.common.user.domain.entity.ItemConfig;
import com.wang.easychat.common.user.domain.enums.ItemTypeEnum;
import com.wang.easychat.common.user.service.IItemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/16
 **/
@Component
public class ItemCache {

    @Autowired
    private IItemConfigService itemConfigService;

    @Cacheable(cacheNames = "item", key = "'itemsByType:' + #itemType")  // 获取缓存，如果没有走下面步骤
    public List<ItemConfig> getByType(Integer itemType){
        return itemConfigService.getByType(itemType);
    }
//    @CachePut   // 主动重新刷新缓存
    @CacheEvict(cacheNames = "item", key = "'itemsByType:' + #itemType") // 清空缓存
    public void evictByType(Integer itemType){
        return;
    }

    @Cacheable(cacheNames = "item", key = "'item:'+#itemId")
    public ItemConfig getById(Long itemId) {
        return itemConfigService.getById(itemId);
    }
}
