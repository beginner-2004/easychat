package com.wang.easychat.common.common.utils.discover;

import cn.hutool.core.util.StrUtil;
import org.jsoup.nodes.Document;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassDescription: 具有优先级的title查询器
 * @Author:Wangzd
 * @Date: 2024/12/8
 **/
public class PrioritizedUrlDiscover extends AbstractUrlDiscover{

    private final List<UrlDiscover> urlDiscovers = new ArrayList<>(2);

    public PrioritizedUrlDiscover() {
        urlDiscovers.add(new WxUrlDiscover());
        urlDiscovers.add(new CommonUrlDiscover());
    }

    /**
     * 责任链模式获取对应信息
     */
    @Nullable
    @Override
    public String getTitle(Document document) {
        for (UrlDiscover urlDiscover : urlDiscovers) {
            String urlTitle = urlDiscover.getTitle(document);
            if (StrUtil.isNotBlank(urlTitle)) {
                return urlTitle;
            }
        }
        return null;
    }

    /**
     * 责任链模式获取对应信息
     */
    @Nullable
    @Override
    public String getDescription(Document document) {
        for (UrlDiscover urlDiscover : urlDiscovers) {
            String urlDescription = urlDiscover.getDescription(document);
            if (StrUtil.isNotBlank(urlDescription)) {
                return urlDescription;
            }
        }
        return null;
    }

    /**
     * 责任链模式获取对应信息
     */
    @Nullable
    @Override
    public String getImage(String url, Document document) {
        for (UrlDiscover urlDiscover : urlDiscovers) {
            String urlImage = urlDiscover.getImage(url, document);
            if (StrUtil.isNotBlank(urlImage)) {
                return urlImage;
            }
        }
        return null;
    }
}
