package com.llh.mapper;

import com.github.pagehelper.Page;
import com.llh.annotation.AutoFill;
import com.llh.dto.ProductPageQueryDTO;
import com.llh.entity.Product;
import com.llh.enumeration.OperationType;
import com.llh.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {

    /**
     * 根据分类id查询商品数量
     *
     * @param categoryId
     * @return
     */
    @Select("select count(id) from product where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增商品
     *
     * @param product
     */
    @AutoFill(OperationType.INSERT)
    void insert(Product product);

    /**
     * 商品分页查询
     *
     * @param productPageQueryDTO
     * @return
     */
    Page<ProductVO> pageQuery(ProductPageQueryDTO productPageQueryDTO);

    /**
     * 根据id查询商品
     * @param id
     * @return
     */
    @Select("select * from product where id = #{id}")
    Product getById(Long id);

    /**
     * 批量删除商品
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 修改商品
     */
    @AutoFill(OperationType.UPDATE)
    void update(Product product);

    /**
     * 根据分类查询商品
     */
    List<Product> list(Product product);

    /**
     * 根据comboId，查询所有的商品
     */
    @Select("select d.* from product d left join combo_product sd on d.id = sd.product_id where sd.combo_id = #{comboId}")
    List<Product> getByComboId(Long comboId);

    /**
     * 根据条件统计商品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
