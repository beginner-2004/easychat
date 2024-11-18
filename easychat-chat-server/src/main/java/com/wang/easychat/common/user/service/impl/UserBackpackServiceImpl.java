package com.wang.easychat.common.user.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.wang.easychat.common.common.domain.enums.YesOrNoEnum;
import com.wang.easychat.common.common.service.LockService;
import com.wang.easychat.common.common.utils.AssertUtil;
import com.wang.easychat.common.user.domain.entity.UserBackpack;
import com.wang.easychat.common.user.domain.enums.IdemporentEnum;
import com.wang.easychat.common.user.mapper.UserBackpackMapper;
import com.wang.easychat.common.user.service.IUserBackpackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    @Autowired
    private LockService lockService;

    @Override
    public Integer getCountByValidItemId(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .count();
    }

    @Override
    public UserBackpack getFirstValidItem(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .orderByAsc(UserBackpack::getId)
                .last("limit 1")
                .one();

    }

    @Override
    public boolean useItem(UserBackpack item) {
        return lambdaUpdate()
                .eq(UserBackpack::getItemId, item.getId())
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .set(UserBackpack::getStatus, YesOrNoEnum.YES)
                .update();
    }

    @Override
    public List<UserBackpack> getByItemIds(Long uid, List<Long> itemIds) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .in(UserBackpack::getItemId, itemIds)
                .list();
    }

    /**
     * 给用户发放一个物品
     * @param uid            用户id
     * @param itemId         物品id
     * @param idemporentEnum 幂等类型
     * @param businessId     幂等唯一标识
     */
    @Override
    public void acquireItem(Long uid, Long itemId, IdemporentEnum idemporentEnum, String businessId) {
        String idempotent = getIdempotent(itemId, idemporentEnum, businessId);
        lockService.excuteWithLock("acquireItem_" + idempotent, ()->{
            UserBackpack userBackpack = getByIdempotent(idempotent);
            if (Objects.nonNull(userBackpack)){
                return;
            }
            // todo 业务检查
            // 发放物品
            UserBackpack insert = UserBackpack.builder()
                    .uid(uid)
                    .itemId(itemId)
                    .status(YesOrNoEnum.NO.getStatus())
                    .idempotent(idempotent)
                    .build();
            save(insert);
            return;
        });
    }

    public UserBackpack getByIdempotent(String idempotent) {
        return lambdaQuery()
                .eq(UserBackpack::getIdempotent, idempotent)
                .one();
    }

    private String getIdempotent(Long itemId, IdemporentEnum idemporentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idemporentEnum.getType(), businessId);
    }
}
