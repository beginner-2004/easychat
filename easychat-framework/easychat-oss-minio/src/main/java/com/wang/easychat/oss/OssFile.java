package com.wang.easychat.oss;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassDescription: OSS文件储存类
 * @Author:Wangzd
 * @Date: 2024/12/9
 **/
@Data
@AllArgsConstructor
public class OssFile {
    /**
     * OSS 存储时文件路径
     */
    String ossFilePath;
    /**
     * 原始文件名
     */
    String originalFileName;
}