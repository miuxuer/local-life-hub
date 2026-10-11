package com.llh.controller.user;

import com.llh.constant.StatusConstant;
import com.llh.dto.ProductSearchDTO;
import com.llh.entity.Product;
import com.llh.result.PageResult;
import com.llh.result.Result;
import com.llh.service.ProductSearchService;
import com.llh.service.ProductService;
import com.llh.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userProductController")
@RequestMapping("/user/dish")
@Slf4j
@Tag(name = "C端-商品浏览接口")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductSearchService productSearchService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询商品
     *
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询商品")
    public Result<List<ProductVO>> list(Long categoryId) {
        //构造redis的key
        String key = "product_" +categoryId;

        //查询redis中是否存在商品数据
        List<ProductVO> list = (List<ProductVO>) redisTemplate.opsForValue().get(key);
        if(list != null && !list.isEmpty()){
            //如果redis中有数据，直接返回，不查数据库
            return Result.success(list);
        }


        Product product = new Product();
        product.setCategoryId(categoryId);
        product.setStatus(StatusConstant.ENABLE);//查询起售中的商品

        //如果redis中没有数据，查数据库
        list = productService.listWithSpec(product);
        //查完数据库后放入redis做热点缓存
        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }

    /**
     * 根据关键字搜索商品
     */
    @GetMapping("/search")
    @Operation(summary = "根据关键字搜索商品")
    public Result<PageResult> search(ProductSearchDTO productSearchDTO) {
        log.info("商品搜索:{}", productSearchDTO);

        //C端只搜索起售中的商品
        productSearchDTO.setStatus(StatusConstant.ENABLE);

        PageResult pageResult = productSearchService.search(productSearchDTO);

        return Result.success(pageResult);
    }

}
