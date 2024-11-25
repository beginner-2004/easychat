package com.wang.easychat.common.user.service.impl;

import com.wang.easychat.common.user.domain.entity.Room;
import com.wang.easychat.common.user.mapper.RoomMapper;
import com.wang.easychat.common.user.service.IRoomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 房间表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-25
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements IRoomService {

}
