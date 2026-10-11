package com.llh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.llh.entity.ComboProduct;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ComboDTO implements Serializable {

    private Long id;

    //分类id
    private Long categoryId;

    //套餐名称
    private String name;

    //套餐价格
    private BigDecimal price;

    //状态 0:停用 1:启用
    private Integer status;

    //描述信息
    private String description;

    //图片
    private String image;

    //套餐商品关系
    @JsonProperty("setmealDishes")
    private List<ComboProduct> comboProducts = new ArrayList<>();

}
