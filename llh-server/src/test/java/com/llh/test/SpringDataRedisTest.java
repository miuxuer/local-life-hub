package com.llh.test;

import com.mysql.cj.util.TimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

// @SpringBootTest
public class SpringDataRedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void redisTest(){
        System.out.println(redisTemplate);
    }

    /**
     * 操作字符串类型数据
     */
    @Test
    public void stringTest(){
        redisTemplate.opsForValue().set("xiaoming",2);
        String str = redisTemplate.opsForValue().get("xiaoming").toString();
        System.out.println(str);
        //设置过期时间
        redisTemplate.opsForValue().set("xiaobai", 30, 1, TimeUnit.MINUTES);
        redisTemplate.opsForValue().setIfAbsent("xiaobai", 1);
        System.out.println(redisTemplate.opsForValue().get("xiaobai"));

    }
}
