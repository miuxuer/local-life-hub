package com.llh.service;

import com.llh.dto.ProductSearchDTO;
import com.llh.entity.Product;
import com.llh.result.PageResult;

import java.util.List;

/**
 * 商品搜索相关接口
 */
public interface ProductSearchService {

    /**
     * 根据关键字搜索商品
     */
    PageResult search(ProductSearchDTO productSearchDTO);

    /**
     * 重建全部商品的索引
     */
    void syncAll();

    /**
     * 同步单个商品的索引，商品新增或修改后调用
     */
    void syncProduct(Product product);

    /**
     * 删除商品的索引，商品删除后调用
     */
    void deleteByIds(List<Long> ids);

}
