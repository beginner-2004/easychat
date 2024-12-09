package com.wang.easychat;

import com.wang.easychat.common.EasychatCustomApplication;
import com.wang.easychat.common.common.constant.RedisKey;
import com.wang.easychat.common.common.utils.JwtUtils;
import com.wang.easychat.common.common.utils.RedisUtils;
import com.wang.easychat.common.user.domain.entity.Black;
import com.wang.easychat.common.user.domain.enums.IdemporentEnum;
import com.wang.easychat.common.user.domain.enums.ItemEnum;
import com.wang.easychat.common.user.service.IBlackService;
import com.wang.easychat.common.user.service.IUserBackpackService;
import com.wang.easychat.common.user.service.IUserService;
import com.wang.easychat.common.user.service.LoginService;
import com.wang.easychat.oss.domain.OssReq;
import com.wang.easychat.oss.service.MinIOTemplate;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/8
 **/
@SpringBootTest(classes = EasychatCustomApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class MapeprTest {

    public static final Long UID = 20002L;

    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private IUserBackpackService userBackpackService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private LoginService loginService;
    @Autowired
    private IBlackService blackService;
    @Autowired
    private IUserService userService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private MinIOTemplate minIOTemplate;

    @Test
    public void sendMQ(){
        Message<String> build = MessageBuilder.withPayload("123").build();
        rocketMQTemplate.send("test1-topic", build);
    }


    @Test
    public void redisLock(){
        RLock lock = redissonClient.getLock("123");
        lock.lock();
        System.out.println();
        lock.unlock();
    }

    @Test
    public void jwt(){
        System.out.println(jwtUtils.createToken(1L));
        System.out.println(jwtUtils.createToken(1L));
        System.out.println(jwtUtils.createToken(1L));
    }

    @Test
    public void thread() throws InterruptedException {
        threadPoolTaskExecutor.execute(() -> {
            if (1 == 1){
                log.error("123");
                throw new RuntimeException("1324");
            }
        });
        Thread.sleep(200);
    }

    @Test
    public void redis(){
        String s = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjIwMDAxLCJjcmVhdGVUaW1lIjoxNzMxNDAyMjc2fQ.5mobWTlPgnmOMU2GDWL-eDca8w3LuxZgUrHFZIgw71w";
        Long validUid = loginService.getValidUid(s);
        System.out.println(validUid);
    }

    @Test
    public void test() throws WxErrorException {
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(1, 10000);
        String url = wxMpQrCodeTicket.getUrl();
        System.out.println(url);
    }

    @Test
    public void acquireItem(){
        userBackpackService.acquireItem(UID, ItemEnum.REG_TOP10_BADGE.getId(), IdemporentEnum.UID, UID+"");
    }

    @Test
    public void delRedis(){
        RedisUtils.del(RedisKey.getKey(RedisKey.WAIT_LOGIN_USER_CODE, 20009));
    }

    @Test
    public void userCache(){
        Map<Integer, List<Black>> collect = blackService.list().stream().collect(Collectors.groupingBy(Black::getType));
        Set<Map.Entry<Integer, List<Black>>> entries = collect.entrySet();
        System.out.println(entries);
    }

    @Test
    public void getUploadUrl() {
        OssReq ossReq = OssReq.builder()
                .fileName("test.png")
                .filePath("/test")
                .autoPath(false)
                .build();

    }

}
