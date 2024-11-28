package com.wang.easychat.common.user.controller;


import com.wang.easychat.common.common.domain.vo.resp.ApiResult;
import com.wang.easychat.common.common.utils.AssertUtil;
import com.wang.easychat.common.common.utils.RequestHolder;
import com.wang.easychat.common.user.domain.dto.ItemInfoDTO;
import com.wang.easychat.common.user.domain.dto.SummeryInfoDTO;
import com.wang.easychat.common.user.domain.enums.RoleEnum;
import com.wang.easychat.common.user.domain.vo.req.user.*;
import com.wang.easychat.common.user.domain.vo.resp.user.BadgeResp;
import com.wang.easychat.common.user.domain.vo.resp.user.UserInfoResp;
import com.wang.easychat.common.user.service.IRoleService;
import com.wang.easychat.common.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author wang
 * @since 2024-11-08
 */
@RestController
@RequestMapping("/capi/user")
@Api(tags = "用户管理相关接口")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IRoleService roleService;

    @GetMapping("/userInfo")
    @ApiOperation("用户详情")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PutMapping("/name")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq modifyNameReq) {
        userService.modifyName(RequestHolder.get().getUid(), modifyNameReq.getName());
        return ApiResult.success();
    }

    @GetMapping("/badges")
    @ApiOperation("可选徽章列表预览")
    public ApiResult<List<BadgeResp>> badges(){
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PutMapping("/badge")
    @ApiOperation("佩戴徽章")
    public ApiResult<Void> wearingBadge(@Valid @RequestBody WearingBadgeReq req){
        userService.wearingBadge(RequestHolder.get().getUid(), req.getBadgeId());
        return ApiResult.success();
    }

    @PutMapping("/black")
    @ApiOperation("拉黑用户")
    public ApiResult<Void> black(@Valid @RequestBody BlackReq req){
        Long uid = RequestHolder.get().getUid();
        boolean hasPower = roleService.hasPower(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasPower, "简聊管理员没有拉黑权限，可以向超管申请");
        userService.black(req);

        return ApiResult.success();
    }

    @PostMapping("/public/summary/userInfo/batch")
    @ApiOperation("用户聚合信息-返回的代表需要刷新的")
    public ApiResult<List<SummeryInfoDTO>> getSummeryUserInfo(@Valid @RequestBody SummeryInfoReq req) {
        return ApiResult.success(userService.getSummeryUserInfo(req));
    }

    @PostMapping("/public/badges/batch")
    @ApiOperation("徽章聚合信息-返回的代表需要刷新的")
    public ApiResult<List<ItemInfoDTO>> getItemInfo(@Valid @RequestBody ItemInfoReq req) {
        return ApiResult.success(userService.getItemInfo(req));
    }

}