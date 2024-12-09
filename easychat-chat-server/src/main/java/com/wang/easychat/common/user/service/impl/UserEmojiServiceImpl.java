package com.wang.easychat.common.user.service.impl;

import com.wang.easychat.common.common.annotation.RedissonLock;
import com.wang.easychat.common.common.domain.vo.resp.ApiResult;
import com.wang.easychat.common.common.domain.vo.resp.IdRespVO;
import com.wang.easychat.common.common.utils.AssertUtil;
import com.wang.easychat.common.user.domain.entity.UserEmoji;
import com.wang.easychat.common.user.domain.vo.req.user.UserEmojiReq;
import com.wang.easychat.common.user.domain.vo.resp.user.UserEmojiResp;
import com.wang.easychat.common.user.mapper.UserEmojiMapper;
import com.wang.easychat.common.user.service.IUserEmojiService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表情包 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-12-09
 */
@Service
public class UserEmojiServiceImpl extends ServiceImpl<UserEmojiMapper, UserEmoji> implements IUserEmojiService {

    /**
     * 根据uid查询表情包
     *
     * @param uid
     * @return
     */
    @Override
    public List<UserEmojiResp> listByUid(Long uid) {
        List<UserEmoji> emojiList = lambdaQuery()
                .eq(UserEmoji::getUid, uid)
                .list();
        return emojiList.stream().map(a -> UserEmojiResp.builder()
                .id(a.getId())
                .expressionUrl(a.getExpressionUrl())
                .build())
                .collect(Collectors.toList());
    }

    /**
     * 新增表情包
     * @param req
     * @param uid
     * @return
     */
    @Override
    @RedissonLock(key = "#uid")
    public ApiResult<IdRespVO> insert(UserEmojiReq req, Long uid) {
        // 检查表情包是否超过30
        int count = countByUid(uid);
        AssertUtil.isFalse(count > 30, "最多只能添加30个表情包！");
        // 检验表情是否存在
        Integer existsCount = countByExpressionUrl(req.getExpressionUrl());
        AssertUtil.isFalse(existsCount > 0, "当前表情已经存在!");
        UserEmoji insert = UserEmoji.builder()
                .uid(uid)
                .expressionUrl(req.getExpressionUrl())
                .build();
        save(insert);
        return ApiResult.success(IdRespVO.id(insert.getId()));
    }

    /**
     * 根据uid和表情包id删除
     *
     * @param id
     * @param uid
     */
    @Override
    public void removeByUidAndId(long id, Long uid) {
        UserEmoji userEmoji = getById(id);
        AssertUtil.isNotEmpty(userEmoji, "表情不能为空");
        AssertUtil.equal(userEmoji.getUid(), uid, "不能删除别人的表情包！");
        removeById(id);
    }

    private Integer countByExpressionUrl(String expressionUrl) {
        return lambdaQuery()
                .eq(UserEmoji::getExpressionUrl, expressionUrl)
                .count();
    }

    private int countByUid(Long uid) {
        return lambdaQuery()
                .eq(UserEmoji::getUid, uid)
                .count();
    }


}
