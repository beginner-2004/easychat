package com.wang.easychat.common.user.service;

import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.user.domain.dto.ItemInfoDTO;
import com.wang.easychat.common.user.domain.dto.SummeryInfoDTO;
import com.wang.easychat.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.easychat.common.user.domain.enums.ChatActiveStatusEnum;
import com.wang.easychat.common.user.domain.vo.req.user.BlackReq;
import com.wang.easychat.common.user.domain.vo.req.user.ItemInfoReq;
import com.wang.easychat.common.user.domain.vo.req.user.SummeryInfoReq;
import com.wang.easychat.common.user.domain.vo.resp.user.BadgeResp;
import com.wang.easychat.common.user.domain.vo.resp.user.UserInfoResp;
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

    void black(BlackReq req);

    void invalidUid(Long id);

    List<User> getFriendList(List<Long> friendUids);

    List<User> getByIds(List<Long> userIds);

    void setUserActiveStatus(Long uid, Integer status);

    /**
     * 获取用户汇总信息
     */
    List<SummeryInfoDTO> getSummeryUserInfo(SummeryInfoReq req);

    List<ItemInfoDTO> getItemInfo(ItemInfoReq req);

    /**
     * 获取传入列表中在线人数
     * @param memberUidList
     * @return
     */
    Integer getOnlineCount(List<Long> memberUidList);

    /**
     * 查找用户
     * @return
     */
    List<User> getMemberList();

    /**
     * 游标翻页查找用户
     * @param memberUidList
     * @param req
     * @param online
     * @return
     */
    CursorPageBaseResp<User> getCursorPage(List<Long> memberUidList, CursorPageBaseReq req, ChatActiveStatusEnum online);
}
