package com.wang.easychat.common.chat.controller;


import com.wang.easychat.common.chat.domain.vo.req.ContactFriendReq;
import com.wang.easychat.common.chat.domain.vo.resp.ChatRoomResp;
import com.wang.easychat.common.chat.service.IRoomAppService;
import com.wang.easychat.common.chat.service.IRoomService;
import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.req.IdReqVO;
import com.wang.easychat.common.common.domain.vo.resp.ApiResult;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.common.utils.RequestHolder;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * 会话列表 前端控制器
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
@RestController
@RequestMapping("/capi/chat")
public class ContactController {

    @Autowired
    private IRoomAppService roomAppService;

    @GetMapping("/public/contact/page")
    @ApiOperation("会话列表")
    public ApiResult<CursorPageBaseResp<ChatRoomResp>> getRoomPage(@Valid CursorPageBaseReq request){
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomAppService.getContactPage(request, uid));
    }

    @GetMapping("/public/contact/detail")
    @ApiOperation("会话详情")
    public ApiResult<ChatRoomResp> getContactDetail(@Valid IdReqVO request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomAppService.getContactDetail(uid, request.getId()));
    }


    @GetMapping("/public/contact/detail/friend")
    @ApiOperation("会话详情(联系人列表发消息用)")
    public ApiResult<ChatRoomResp> getContactDetailByFriend(@Valid ContactFriendReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomAppService.getContactDetailByFriend(uid, request.getUid()));
    }

}
