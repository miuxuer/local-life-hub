package com.llh.controller.admin;

import com.llh.dto.ProductDTO;
import com.llh.dto.ProductPageQueryDTO;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 商品相关接口
 */

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Tag(name = "商品相关接口")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductSearchService productSearchService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增商品
     */
    @PostMapping
    @Operation(summary = "新增商品")
    public Result save(@RequestBody ProductDTO productDTO) {
        log.info("新增商品:{}", productDTO);

        productService.saveWithSpec(productDTO);
        //清理指定缓存数据
        String key = "product_" + productDTO.getCategoryId();
        cleanCache(key);

        return Result.success();

    }

    /**
     * 商品分页查询
     */
    @Operation(summary = "商品分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(ProductPageQueryDTO productPageQueryDTO) {
        log.info("商品分页查询", productPageQueryDTO);

        PageResult pageResult = productService.pageQuery(productPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 批量删除商品
     */
    @Operation(summary = "批量删除商品")
    @DeleteMapping
    public Result deleteBatch(@RequestParam List<Long> ids) {
        log.info("批量删除商品:{}", ids);

        productService.deleteBatch(ids);
        //清理redis中以product_开头的全部缓存
        cleanCache("product_*");

        return Result.success();
    }

    /**
     * 根据id查询商品
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询商品")
    public Result<ProductVO> getById(@PathVariable Long id) {
        log.info("根据id查询商品:{}", id);

        ProductVO productVO = productService.getByIdWithSpec(id);

        return Result.success(productVO);
    }

    /**
     * 修改商品
     */
    @PutMapping
    @Operation(summary = "修改商品")
    public Result update(@RequestBody ProductDTO productDTO) {
        log.info("修改商品:{}", productDTO);

        productService.updateWithSpec(productDTO);
        //清理redis中以product_开头的全部缓存
        cleanCache("product_*");

        return Result.success();
    }

    /**
     * 根据分类id查询商品
     */
    @Operation(summary = "根据分类id查询商品")
    @GetMapping("/list")
    public Result<List<Product>> list(Long categoryId) {
        log.info("根据分类id查询商品:{}", categoryId);

        List<Product> list = productService.list(categoryId);

        return Result.success(list);
    }

    /**
     * 商品起售、停售
     */
    @Operation(summary = "商品起售、停售")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("商品起售、停售:{}, {}", status, id);

        productService.startOrStop(status, id);
        //清理redis中以product_开头的全部缓存
        cleanCache("product_*");

        return Result.success();
    }

    /**
     * 商品搜索
     */
    @Operation(summary = "商品搜索")
    @GetMapping("/search")
    public Result<PageResult> search(ProductSearchDTO productSearchDTO) {
        log.info("商品搜索:{}", productSearchDTO);

        PageResult pageResult = productSearchService.search(productSearchDTO);

        return Result.success(pageResult);
    }

    /**
     * 重建全部商品的索引
     */
    @Operation(summary = "重建商品索引")
    @PostMapping("/search/sync")
    public Result syncSearchIndex() {
        log.info("重建商品索引");

        productSearchService.syncAll();

        return Result.success();
    }

    /**
     * 清理redis缓存
     */
    private void cleanCache(String patten) {
        Set keys = redisTemplate.keys(patten);
        redisTemplate.delete(keys);

    }


}
