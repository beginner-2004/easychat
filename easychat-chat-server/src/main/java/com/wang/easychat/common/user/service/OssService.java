package com.wang.easychat.common.user.service;

import com.wang.easychat.common.user.domain.vo.req.oss.UploadUrlReq;
import com.wang.easychat.oss.domain.OssResp;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/9
 **/
public interface OssService {
    /**
     * 获取临时的上传链接
     */
    OssResp getUploadUrl(Long uid, UploadUrlReq req);
}
