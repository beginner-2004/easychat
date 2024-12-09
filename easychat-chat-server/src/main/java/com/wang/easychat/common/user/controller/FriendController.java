package com.wang.easychat.common.user.controller;


import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.req.PageBaseReq;
import com.wang.easychat.common.common.domain.vo.resp.ApiResult;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.common.domain.vo.resp.PageBaseResp;
import com.wang.easychat.common.common.utils.RequestHolder;
import com.wang.easychat.common.user.domain.vo.req.friend.FriendApplyReq;
import com.wang.easychat.common.user.domain.vo.req.friend.FriendApproveReq;
import com.wang.easychat.common.user.domain.vo.req.friend.FriendCheckReq;
import com.wang.easychat.common.user.domain.vo.req.friend.FriendDeleteReq;
import com.wang.easychat.common.user.domain.vo.resp.friend.FriendApplyResp;
import com.wang.easychat.common.user.domain.vo.resp.friend.FriendCheckResp;
import com.wang.easychat.common.user.domain.vo.resp.friend.FriendResp;
import com.wang.easychat.common.user.domain.vo.resp.friend.FriendUnreadResp;
import com.wang.easychat.common.user.service.IUserFriendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.validation.Valid;

/**
 * <p>
 * 用户联系人表 前端控制器
 * </p>
 *
 * @author wang
 * @since 2024-11-25
 */
@RestController
@RequestMapping("/capi/user/friend")
@Api(tags = "用户好友相关接口")
public class FriendController {

    @Autowired
    private IUserFriendService userFriendService;

    @GetMapping("/check")
    @ApiOperation("批量判断是否是自己好友")
    public ApiResult<FriendCheckResp> check(@Valid FriendCheckReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.check(uid, request));
    }


    @PostMapping("/apply")
    @ApiOperation("申请好友")
    public ApiResult<Void> apply(@Valid @RequestBody FriendApplyReq request) {
        Long uid = RequestHolder.get().getUid();
        userFriendService.apply(uid, request);
        return ApiResult.success();
    }

    @PutMapping("/apply")
    @ApiOperation("审批同意")
    public ApiResult<Void> applyApprove(@Valid @RequestBody FriendApproveReq request) {
        userFriendService.applyApprove(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }



    @GetMapping("/apply/page")
    @ApiOperation("好友申请列表")
    public ApiResult<PageBaseResp<FriendApplyResp>> page(@Valid PageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.pageApplyFriend(uid, request));
    }

    @GetMapping("/apply/unread")
    @ApiOperation("申请未读数")
    public ApiResult<FriendUnreadResp> unread() {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.unread(uid));
    }

    @DeleteMapping()
    @ApiOperation("逻辑删除好友(双向删除)")
    public ApiResult<Void> delete(@Valid @RequestBody FriendDeleteReq request) {
        Long uid = RequestHolder.get().getUid();
        userFriendService.deleteFriend(uid, request.getTargetUid());
        return ApiResult.success();
    }




    @GetMapping("/page")
    @ApiOperation("联系人列表")
    public ApiResult<CursorPageBaseResp<FriendResp>> friendList(@Valid CursorPageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.friendList(uid, request));
    }
}
