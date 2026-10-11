package com.llh.mapper;

import com.github.pagehelper.Page;
import com.llh.annotation.AutoFill;
import com.llh.dto.ComboPageQueryDTO;
import com.llh.entity.Combo;
import com.llh.enumeration.OperationType;
import com.llh.vo.ProductItemVO;
import com.llh.vo.ComboVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ComboMapper {

    /**
     * 根据分类id查询套餐的数量
     *
     */
    @Select("select count(id) from combo where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    @AutoFill(OperationType.INSERT)
    @Insert("insert into combo (category_id, name, price, status, description, image, create_time, update_time, create_user, update_user) " +
            "VALUES" +
            "(#{categoryId}, #{name}, #{price}, #{status}, #{description}, #{image}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Combo combo);

    /**
     * 分页查询
     */
    Page<ComboVO> pageQuery(ComboPageQueryDTO comboPageQueryDTO);

    /**
     * 批量删除套餐
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据id查询套餐
     */
    @Select("select * from combo where id = #{id}")
    Combo getById(Long id);

    /**
     * 修改套餐
     * @param combo
     */
    @AutoFill(OperationType.UPDATE)
    void update(Combo combo);

    /**
     * 动态条件查询套餐
     */
    List<Combo> list(Combo combo);

    /**
     * 根据套餐id查询商品选项
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from combo_product sd left join product d on sd.product_id = d.id " +
            "where sd.combo_id = #{comboId}")
    List<ProductItemVO> getProductItemByComboId(Long comboId);

    /**
     * 根据条件统计套餐数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
