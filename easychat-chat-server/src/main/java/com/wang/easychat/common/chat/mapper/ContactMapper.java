package com.wang.easychat.common.chat.mapper;

import com.wang.easychat.common.chat.domain.entity.Contact;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 会话列表 Mapper 接口
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
public interface ContactMapper extends BaseMapper<Contact> {

    void refreshOrCreateActiveTime(@Param("roomId") Long roomId, @Param("memberUidList") List<Long> memberUidList, @Param("msgId") Long msgId, @Param("activeTime") Date activeTime);
}
