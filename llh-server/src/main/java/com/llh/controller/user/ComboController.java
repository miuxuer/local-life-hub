package com.llh.controller.user;

import com.llh.constant.StatusConstant;
import com.llh.entity.Combo;
import com.llh.result.Result;
import com.llh.service.ComboService;
import com.llh.vo.ProductItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userComboController")
@RequestMapping("/user/setmeal")
@Tag(name = "C端-套餐浏览接口")
public class ComboController {
    @Autowired
    private ComboService comboService;

    /**
     * 条件查询
     *
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询套餐")
    @Cacheable(cacheNames = "comboCache", key = "#categoryId")  //缓存套餐到redis，key为key:categoryId::100，具体的值为value:Result对象
    public Result<List<Combo>> list(Long categoryId) {
        Combo combo = new Combo();
        combo.setCategoryId(categoryId);
        combo.setStatus(StatusConstant.ENABLE);

        List<Combo> list = comboService.list(combo);
        return Result.success(list);
    }

    /**
     * 根据套餐id查询包含的商品列表
     *
     */
    @GetMapping("/dish/{id}")
    @Operation(summary = "根据套餐id查询包含的商品列表")
    public Result<List<ProductItemVO>> productList(@PathVariable("id") Long id) {
        List<ProductItemVO> list = comboService.getProductItemById(id);
        return Result.success(list);
    }
}
