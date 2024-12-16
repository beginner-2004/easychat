package com.wang.easychat.common.chat.controller;

import com.wang.easychat.common.chat.domain.dto.MsgReadInfoDTO;
import com.wang.easychat.common.chat.domain.vo.req.*;
import com.wang.easychat.common.chat.domain.vo.resp.ChatMessageReadResp;
import com.wang.easychat.common.chat.domain.vo.resp.ChatMessageResp;
import com.wang.easychat.common.chat.service.ChatService;
import com.wang.easychat.common.common.domain.vo.resp.ApiResult;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.common.utils.RequestHolder;
import com.wang.easychat.common.user.domain.enums.BlackTypeEnum;
import com.wang.easychat.common.user.service.cache.UserCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/29
 **/
@RestController
@RequestMapping("/capi/chat")
@Api(tags = "聊天室相关接口")
@Slf4j
public class ChatController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private UserCache userCache;



    @PostMapping("/msg")
    @ApiOperation("发送消息")
    // todo 频控监测
    public ApiResult<ChatMessageResp> sendMsg(@Valid @RequestBody ChatMessageReq request) {
        Long msgId = chatService.sendMsg(request, RequestHolder.get().getUid());
        //返回完整消息格式，方便前端展示
        return ApiResult.success(chatService.getMsgResp(msgId, RequestHolder.get().getUid()));
    }

    @GetMapping("/public/msg/page")
    @ApiOperation("消息列表")
//    @FrequencyControl(time = 120, count = 20, target = FrequencyControl.Target.IP)
    public ApiResult<CursorPageBaseResp<ChatMessageResp>> getMsgPage(@Valid ChatMessagePageReq request) {
        CursorPageBaseResp<ChatMessageResp> msgPage = chatService.getMsgPage(request, RequestHolder.get().getUid());
        filterBlackMsg(msgPage);
        return ApiResult.success(msgPage);
    }



    /**
     * 过滤黑名单
     * @param msgPage
     */
    private void filterBlackMsg(CursorPageBaseResp<ChatMessageResp> msgPage) {
        Set<String> blackMembers = getBlackUidSet();
        msgPage.getList().removeIf(a -> blackMembers.contains(a.getFromUser().getUid().toString()));
    }

    /**
     * 查询黑名单
     * @return
     */
    private Set<String> getBlackUidSet() {
        return userCache.getBlackMap().getOrDefault(BlackTypeEnum.UID.getType(), new HashSet<>());
    }

    @PutMapping("/msg/recall")
    @ApiOperation("撤回消息")
    public ApiResult<Void> recallMsg(@Valid @RequestBody ChatMessageBaseReq request){
        chatService.recallMsg(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }

    @PutMapping("/msg/mark")
    @ApiOperation("消息标记")
    // todo 频控
    public ApiResult<Void> setMsgMark(@Valid @RequestBody ChatMessageMarkReq request) {
        chatService.setMsgMark(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }


    @GetMapping("/msg/read/page")
    @ApiOperation("消息的已读未读列表")
    public ApiResult<CursorPageBaseResp<ChatMessageReadResp>> getReadPage(@Valid ChatMessageReadReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(chatService.getReadPage(uid, request));
    }

    @GetMapping("/msg/read")
    @ApiOperation("获取消息的已读未读总数")
    public ApiResult<Collection<MsgReadInfoDTO>> getReadInfo(@Valid ChatMessageReadInfoReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(chatService.getMsgReadInfo(uid, request));
    }

    @PutMapping("/msg/read")
    @ApiOperation("消息阅读上报")
    public ApiResult<Void> msgRead(@Valid @RequestBody ChatMessageMemberReq request) {
        Long uid = RequestHolder.get().getUid();
        chatService.msgRead(uid, request);
        return ApiResult.success();
    }

}
