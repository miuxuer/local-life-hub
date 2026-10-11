package com.llh.controller.admin;

import com.llh.dto.ComboDTO;
import com.llh.dto.ComboPageQueryDTO;
import com.llh.result.PageResult;
import com.llh.result.Result;
import com.llh.service.ComboService;
import com.llh.vo.ComboVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理相关接口
 */
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Tag(name = "套餐管理相关接口")
public class ComboController {

    @Autowired
    private ComboService comboService;
    /**
     * 新增套餐
     */
    @PostMapping
    @Operation(summary = "新增套餐")
    @CacheEvict(cacheNames = "comboCache", key = "#comboDTO.categoryId") //根据套餐id删除套餐缓存
    public Result save(@RequestBody ComboDTO comboDTO){
        log.info("新增套餐:{}", comboDTO);

        comboService.saveWithProduct(comboDTO);

        return Result.success();
    }

    /**
     * 分页查询
     */
    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(ComboPageQueryDTO comboPageQueryDTO){
        log.info("分页查询:{}", comboPageQueryDTO);

        PageResult pageResult = comboService.pageQuery(comboPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 批量删除套餐
     */
    @Operation(summary = "批量删除套餐")
    @DeleteMapping
    @CacheEvict(cacheNames = "comboCache", allEntries = true) //删除所有套餐缓存
    public Result deleteBatch(@RequestParam List<Long> ids){
        log.info("批量删除套餐:{}", ids);

        comboService.deleteBatch(ids);

        return Result.success();
    }

    /**
     * 根据id查询套餐
     */
    @Operation(summary = "根据id查询套餐")
    @GetMapping("/{id}")
    public Result<ComboVO> getById(@PathVariable Long id){
        log.info("根据id查询套餐:{}",id);

        ComboVO comboVO = comboService.getByIdWithProduct(id);

        return Result.success(comboVO);
    }

    /**
     * 修改套餐
     */
    @Operation(summary = "修改套餐")
    @PutMapping
    @CacheEvict(cacheNames = "comboCache", allEntries = true) //删除所有套餐缓存
    public Result update(@RequestBody ComboDTO comboDTO){
        log.info("修改套餐");

        comboService.updateWithProduct(comboDTO);

        return Result.success();
    }

    /**
     * 套餐起售、停售
     */
    @Operation(summary = "套餐起售、停售")
    @PostMapping("/status/{status}")
    @CacheEvict(cacheNames = "comboCache", allEntries = true) //删除所有套餐缓存
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("套餐起售、停售:{}, {}", status, id);

        comboService.startOrStop(status, id);

        return Result.success();
    }

}
