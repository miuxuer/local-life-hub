package com.llh.service.impl;

import com.llh.context.BaseContext;
import com.llh.dto.ShoppingCartDTO;
import com.llh.entity.Product;
import com.llh.entity.Combo;
import com.llh.entity.ShoppingCart;
import com.llh.mapper.ProductMapper;
import com.llh.mapper.ComboMapper;
import com.llh.mapper.ShoppingCartMapper;
import com.llh.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ComboMapper comboMapper;

    /**
     * 添加购物车
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //判断当前加入购物车的商品是否存在
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && !list.isEmpty()) {
            ShoppingCart cart = list.get(0);
            //如果存在，则把数量加1
            cart.setNumber(cart.getNumber() + 1);
            //数据跟新到数据库
            shoppingCartMapper.updateNumberById(cart);
        } else {
            //如果不存在，则需要插入一条购物车数据
            //判断productId是否为null，如果为null，则说明添加的是套餐，如果不是null，说明添加的是商品
            Long productId = shoppingCart.getProductId();
            if (productId != null) {
                //添加的是商品
                //根据商品id查询商品信息
                Product product = productMapper.getById(productId);
                shoppingCart.setName(product.getName());
                shoppingCart.setImage(product.getImage());
                shoppingCart.setAmount(product.getPrice());

            } else {
                //添加的是套餐
                //根据套餐id查询套餐
                Combo combo = comboMapper.getById(shoppingCart.getComboId());
                shoppingCart.setName(combo.getName());
                shoppingCart.setImage(combo.getImage());
                shoppingCart.setAmount(combo.getPrice());

            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            //更新购物车（往购物车里添加新的商品）
            shoppingCartMapper.insert(shoppingCart);

        }

    }

    /**
     * 查看购物车
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();

        shoppingCartMapper.deleteByUserId(userId);

    }

    /**
     * 删除购物车中一个商品
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {

        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .productId(shoppingCartDTO.getProductId())
                .productSpec(shoppingCartDTO.getProductSpec())
                .comboId(shoppingCartDTO.getComboId())
                .build();

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        if (list != null && !list.isEmpty()) {
            shoppingCart = list.get(0);
            //如果数量等于1，直接删除改条数据
            if (shoppingCart.getNumber() == 1) {
                shoppingCartMapper.deleteById(shoppingCart.getId());
            } else {
                //如果数量大于1，则减1
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }

    }

}
