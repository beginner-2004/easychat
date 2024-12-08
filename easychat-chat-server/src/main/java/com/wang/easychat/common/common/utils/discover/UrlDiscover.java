package com.wang.easychat.common.common.utils.discover;

import com.wang.easychat.common.common.utils.discover.domain.UrlInfo;
import org.jsoup.nodes.Document;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/8
 **/
public interface UrlDiscover {


    /**
     * 获取url对应的信息
     */
    @Nullable
    Map<String, UrlInfo> getUrlContentMap(String content);

    /**
     * 获取url对应的content
     */
    @Nullable
    UrlInfo getContent(String url);

    /**
     * 获取url对应title
     */
    @Nullable
    String getTitle(Document document);

    /**
     * 获取给定元素的description
     */
    @Nullable
    String getDescription(Document document);

    /**
     * 获取给定元素的img
     */
    @Nullable
    String getImage(String url, Document document);

}
