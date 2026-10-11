package com.llh.mapper;

import com.llh.entity.ComboProduct;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ComboProductMapper {

    /**
     * 根据id批量查找套餐
     */
    List<Long> getComboIdsByProductIds(List<Long> productIds);

    /**
     * 新增套餐与商品关系表
     */
    void insertBatch(List<ComboProduct> comboProducts);

    /**
     * 批量删除套餐
     */
    void deleteByComboIds(List<Long> comboIds);

    /**
     * 根据id查询套餐
     */
    @Select("select * from combo_product where combo_id = #{id}")
    List<ComboProduct> getByComboId(Long id);

    /**
     * 根据id删除套餐
     */
    @Delete("delete from combo_product where combo_id = #{comboId}")
    void deleteByComboId(Long comboId);
}
