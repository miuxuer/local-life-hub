package com.llh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.llh.entity.ProductSpec;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductDTO implements Serializable {

    private Long id;
    //商品名称
    private String name;
    //商品分类id
    private Long categoryId;
    //商品价格
    private BigDecimal price;
    //图片
    private String image;
    //描述信息
    private String description;
    //0 停售 1 起售
    private Integer status;
    //规格
    @JsonProperty("flavors")
    private List<ProductSpec> specs = new ArrayList<>();

}
