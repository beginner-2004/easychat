package com.wang.easychat.common.common.event;

import com.wang.easychat.common.user.domain.dto.GroupDelDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/16
 **/
@Getter
public class DelGroupEvent extends ApplicationEvent {

    private GroupDelDTO groupDelDTO;

    private List<Long> uidList;
    public DelGroupEvent(Object source, GroupDelDTO groupDelDTO) {
        super(source);
        this.groupDelDTO = groupDelDTO;

    }
}
