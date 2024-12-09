package com.wang.easychat.common.user.controller;


import com.wang.easychat.common.common.annotation.RedissonLock;
import com.wang.easychat.common.common.domain.vo.req.IdReqVO;
import com.wang.easychat.common.common.domain.vo.resp.ApiResult;
import com.wang.easychat.common.common.domain.vo.resp.IdRespVO;
import com.wang.easychat.common.common.utils.RequestHolder;
import com.wang.easychat.common.user.domain.vo.req.user.UserEmojiReq;
import com.wang.easychat.common.user.domain.vo.resp.user.UserEmojiResp;
import com.wang.easychat.common.user.service.IUserEmojiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表情包 前端控制器
 * </p>
 *
 * @author wang
 * @since 2024-12-09
 */
@RestController
@RequestMapping("/capi/user/emoji")
@Api(tags = "用户表情包管理相关接口")
public class UserEmojiController {

    @Autowired
    private IUserEmojiService userEmojiService;

    @GetMapping("/list")
    @ApiOperation("表情包列表")
    public ApiResult<List<UserEmojiResp>> getEmojisPage() {
        return ApiResult.success(userEmojiService.listByUid(RequestHolder.get().getUid()));
    }

    @PostMapping()
    @ApiOperation("新增表情包")
    public ApiResult<IdRespVO> insertEmojis(@Valid @RequestBody UserEmojiReq req) {
        return userEmojiService.insert(req, RequestHolder.get().getUid());
    }

    @DeleteMapping()
    @ApiOperation("删除表情包")
    public ApiResult<Void> deleteEmojis(@Valid @RequestBody IdReqVO reqVO) {
        userEmojiService.removeByUidAndId(reqVO.getId(), RequestHolder.get().getUid());
        return ApiResult.success();
    }
}
