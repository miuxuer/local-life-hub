package com.llh.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 套餐商品关系
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComboProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //套餐id
    @JsonProperty("setmealId")
    private Long comboId;

    //商品id
    @JsonProperty("dishId")
    private Long productId;

    //商品名称 （冗余字段）
    private String name;

    //商品原价
    private BigDecimal price;

    //份数
    private Integer copies;
}
