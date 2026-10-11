package com.llh.mapper;

import com.llh.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 查询购物车
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据id更新商品数量
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart cart);

    /**
     * 更新购物车（往购物车里添加新的商品）
     */
    void insert(ShoppingCart shoppingCart);

    /**
     * 清空购物车（根据用户id删除购物车信息）
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 根据id删除购物车数据
     */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);

    /**
     * 再来一单
     */
    void insertBatch(List<ShoppingCart> shoppingCarts);
}
