package com.llh.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品搜索查询参数
 */
@Data
public class ProductSearchDTO implements Serializable {

    //搜索关键字
    private String keyword;

    //分类id
    private Long categoryId;

    //0 停售 1 起售
    private Integer status;

    //页码
    private int page = 1;

    //每页记录数
    private int pageSize = 10;

}
