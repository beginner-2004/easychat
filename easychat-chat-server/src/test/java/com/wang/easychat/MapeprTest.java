package com.wang.easychat;

import com.wang.easychat.common.EasychatCustomApplication;
import com.wang.easychat.common.common.thread.MyUncaughtExceptionHander;
import com.wang.easychat.common.common.utils.JwtUtils;
import com.wang.easychat.common.common.utils.RedisUtils;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.service.IUserService;
import com.wang.easychat.common.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/8
 **/
@SpringBootTest(classes = EasychatCustomApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class MapeprTest {
    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private LoginService loginService;

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

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

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
}
