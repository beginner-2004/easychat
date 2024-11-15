package com.wang.easychat.common.user.controller;


import com.wang.easychat.common.common.domain.vo.resp.ApiResult;
import com.wang.easychat.common.common.utils.RequestHolder;
import com.wang.easychat.common.user.domain.vo.req.ModifyNameReq;
import com.wang.easychat.common.user.domain.vo.resp.UserInfoResp;
import com.wang.easychat.common.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

}