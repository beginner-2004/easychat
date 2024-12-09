package com.wang.easychat.common.user.service.impl;

import com.wang.easychat.common.common.utils.AssertUtil;
import com.wang.easychat.common.user.domain.enums.OssSceneEnum;
import com.wang.easychat.common.user.domain.vo.req.oss.UploadUrlReq;
import com.wang.easychat.common.user.service.OssService;
import com.wang.easychat.oss.domain.OssReq;
import com.wang.easychat.oss.domain.OssResp;
import com.wang.easychat.oss.service.MinIOTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/9
 **/
@Service
public class OssServiceImpl implements OssService {
    @Autowired
    private MinIOTemplate minIOTemplate;

    /**
     * 获取临时的上传链接
     */
    @Override
    public OssResp getUploadUrl(Long uid, UploadUrlReq req) {
        OssSceneEnum sceneEnum = OssSceneEnum.of(req.getScene());
        AssertUtil.isNotEmpty(sceneEnum, "场景有误");
        OssReq ossReq = OssReq.builder()
                .fileName(req.getFileName())
                .filePath(sceneEnum.getPath())
                .uid(uid)
                .build();
        return minIOTemplate.getPreSignedObjectUrl(ossReq);
    }
}
