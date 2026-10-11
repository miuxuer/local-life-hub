package com.llh.mapper;

import com.llh.entity.ProductSpec;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductSpecMapper {
    /**
     * 添加规格
     */
    void insertBatch(List<ProductSpec> specs);

    /**
     * 批量删除商品规格
     *
     */
    void deleteByProductIds(List<Long> productIds);

    /**
     * 根据id查询规格
     */
    @Select("select * from product_spec where product_id = #{productId}")
    List<ProductSpec> getByProductId(Long productId);


    /**
     * 根据id删除规格
     * @param productId
     */
    @Delete("delete from product_spec where product_id = #{productId}")
    void deleteByProductId(Long productId);
}
