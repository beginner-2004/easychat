package com.wang.easychat.common.chat.controller;

import com.wang.easychat.common.chat.domain.vo.req.GroupAddReq;
import com.wang.easychat.common.chat.domain.vo.resp.MemberResp;
import com.wang.easychat.common.chat.service.IRoomAppService;
import com.wang.easychat.common.common.domain.vo.req.IdReqVO;
import com.wang.easychat.common.common.domain.vo.resp.ApiResult;
import com.wang.easychat.common.common.domain.vo.resp.IdRespVO;
import com.wang.easychat.common.common.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @ClassDescription: 房间相关接口
 * @Author:Wangzd
 * @Date: 2024/12/13
 **/
@RestController
@Api(tags = "聊天室相关接口")
@Slf4j
@RequestMapping("/capi/room")
public class RoomController {

    @Autowired
    private IRoomAppService roomAppService;

    @GetMapping("/public/group")
    @ApiOperation("群组详情")
    public ApiResult<MemberResp> groupDetail(@Valid IdReqVO request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomAppService.getGroupDetail(uid, request.getId()));
    }

    @PostMapping("/group")
    @ApiOperation("新增群组")
    public ApiResult<IdRespVO> addGroup(@Valid @RequestBody GroupAddReq request) {
        Long uid = RequestHolder.get().getUid();
        Long roomId = roomAppService.addGroup(uid, request);
        return ApiResult.success(IdRespVO.id(roomId));
    }

}


