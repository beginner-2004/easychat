package com.wang.easychat.common.user.service;

import com.wang.easychat.common.common.domain.vo.resp.ApiResult;
import com.wang.easychat.common.common.domain.vo.resp.IdRespVO;
import com.wang.easychat.common.user.domain.entity.UserEmoji;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.easychat.common.user.domain.vo.req.user.UserEmojiReq;
import com.wang.easychat.common.user.domain.vo.resp.user.UserEmojiResp;

import java.util.List;

/**
 * <p>
 * 用户表情包 服务类
 * </p>
 *
 * @author wang
 * @since 2024-12-09
 */
public interface IUserEmojiService extends IService<UserEmoji> {

    /**
     * 根据uid查询表情包
     * @param uid
     * @return
     */
    List<UserEmojiResp> listByUid(Long uid);

    /**
     * 新增表情包
     * @param req
     * @param uid
     * @return
     */
    ApiResult<IdRespVO> insert(UserEmojiReq req, Long uid);

    /**
     * 根据uid和表情包id删除
     * @param id
     * @param uid
     */
    void removeByUidAndId(long id, Long uid);
}
