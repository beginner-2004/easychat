package com.wang.easychat.oss.service;

import com.wang.easychat.oss.domain.OssReq;
import com.wang.easychat.oss.domain.OssResp;
import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @ClassDescription: 对象存储操作类
 * @Author:Wangzd
 * @Date: 2024/12/8
 **/
public interface OssTemplate {

    /**
     * 查询所有存储桶
     */
    public List<Bucket> listBuckets();

    /**
     * 桶是否存在
     */
    public boolean bucketExists(String bucketName);

    /**
     * 创建存储桶
     */
    public void makeBucket(String bucketName);

    /**
     * 删除一个空桶 如果存储桶存在对象不为空时，删除会报错。
     */
    public void removeBucket(String bucketName) ;

    /**
     * 返回临时带签名、过期时间一天、PUT请求方式的访问URL
     */
    public OssResp getPreSignedObjectUrl(OssReq req);

    /**
     * GetObject接口用于获取某个文件（Object）。此操作需要对此Object具有读权限。
     */
    public InputStream getObject(String bucketName, String ossFilePath);

    /**
     * 查询桶的对象信息
     */
    public Iterable<Result<Item>> listObjects(String bucketName, boolean recursive);

    /**
     * 生成随机文件名，防止重复
     */
    public String generateAutoPath(OssReq req);

    /**
     * 获取带签名的临时上传元数据对象，前端可获取后，直接上传到Minio
     */
    public Map<String, String> getPreSignedPostFormData(String bucketName, String fileName);
}
