package com.llh.controller.user;

import com.llh.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 营业状态相关接口
 */
@Slf4j
@RestController("userShopController")
@RequestMapping("/user/shop")
@Tag(name = "营业状态相关接口")
public class ShopController {

    private static final String KEY = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取营业状态
     */
    @Operation(summary = "获取营业状态")
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);

        log.info("营业状态:{}", status == 1 ? "营业中" : "已打烊");

        return Result.success(status);
    }

}
