package com.wang.easychat.common.chat.consumer;

import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.entity.Room;
import com.wang.easychat.common.chat.domain.entity.RoomFriend;
import com.wang.easychat.common.chat.domain.enums.RoomTypeEnum;
import com.wang.easychat.common.chat.domain.vo.resp.ChatMessageResp;
import com.wang.easychat.common.chat.service.*;
import com.wang.easychat.common.chat.service.cache.GroupMemberCache;
import com.wang.easychat.common.chat.service.cache.HotRoomCache;
import com.wang.easychat.common.chat.service.cache.RoomCache;
import com.wang.easychat.common.common.constant.MQConstant;
import com.wang.easychat.common.common.domain.dto.MsgSendMessageDTO;
import com.wang.easychat.common.user.service.adapter.WSAdapter;
import com.wang.easychat.common.user.service.impl.PushService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/4
 **/
@RocketMQMessageListener(consumerGroup = MQConstant.SEND_MSG_GROUP, topic = MQConstant.SEND_MSG_TOPIC)
@Component
public class MsgSendConsumer implements RocketMQListener<MsgSendMessageDTO> {

    @Autowired
    private IMessageService messageService;
    @Autowired
    private RoomCache roomCache;
    @Autowired
    private ChatService chatService;
    @Autowired
    private IRoomService roomService;
    @Autowired
    private PushService pushService;
    @Autowired
    private HotRoomCache hotRoomCache;
    @Autowired
    private GroupMemberCache groupMemberCache;
    @Autowired
    private IRoomFriendService roomFriendService;
    @Autowired
    private IContactService contactService;

    @Override
    public void onMessage(MsgSendMessageDTO msgSendMessageDTO) {
        Message msg = messageService.getById(msgSendMessageDTO.getMsgId());
        Room room = roomCache.get(msg.getRoomId());
        ChatMessageResp msgResp = chatService.getMsgResp(msg, null);
        // 所有房间给那些房间最新消息
        roomService.refreshActiveTime(room.getId(), msg.getId(), msg.getCreateTime());
        roomCache.delete(room.getId());
        if (room.isHotRoom()){// 总群则将消息推送给所有在线的人
            // 更新缓存
            hotRoomCache.refreshActiveTime(room.getId(), msg.getCreateTime());
            // 推送
            pushService.sendPushMsg(WSAdapter.buildMsgSend(msgResp));
        } else {
            List<Long> memberUidList = new ArrayList<>();
            if (Objects.equals(room.getType(), RoomTypeEnum.GROUP.getType())){
                // 群聊推给所有用户
                memberUidList = groupMemberCache.getMemberUidList(room.getId());
            }else if (Objects.equals(room.getType(), RoomTypeEnum.FRIEND.getType())){
                // 对两人推送
                RoomFriend roomFriend = roomFriendService.getByRoomId(room.getId());
                memberUidList = Arrays.asList(roomFriend.getUid1(), roomFriend.getUid2());
            }
            // 更新群成员的会话时间
            contactService.refreshOrCreateActiveTime(room.getId(), memberUidList, msg.getId(), msg.getCreateTime());
            // 推送给群成员
            pushService.sendPushMsg(WSAdapter.buildMsgSend(msgResp), memberUidList);
        }
    }
}
