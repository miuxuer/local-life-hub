package com.llh.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品搜索索引文档
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    //商品id
    private Long id;

    //商品名称
    private String name;

    //分类id
    private Long categoryId;

    //商品价格
    private Double price;

    //图片
    private String image;

    //描述信息
    private String description;

    //0 停售 1 起售
    private Integer status;

    //命中的关键字在商品名称中的高亮片段
    private String highlightName;

}
