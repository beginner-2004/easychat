package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.entity.MessageMark;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 消息标记表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
public interface IMessageMarkService extends IService<MessageMark> {

    /**
     * 根据 id集合 获取 MessageMark
     * @param collect
     * @return
     */
    List<MessageMark> getValidMarkByMsgIdBatch(List<Long> collect);

    /**
     * 获取标记数据
     * @return
     */
    MessageMark get(Long uid, Long msgId, Integer markType);

    /**
     * 获取标记次数
     * @param msgId
     * @param markType
     * @return
     */
    Integer getMarkCount(Long msgId, Integer markType);
}
