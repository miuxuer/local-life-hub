package com.llh.service;

import com.llh.dto.ProductDTO;
import com.llh.dto.ProductPageQueryDTO;
import com.llh.entity.Product;
import com.llh.result.PageResult;
import com.llh.vo.ProductVO;

import java.util.List;

public interface ProductService {
    void saveWithSpec(ProductDTO productDTO);

    PageResult pageQuery(ProductPageQueryDTO productPageQueryDTO);

    void deleteBatch(List<Long> ids);

    ProductVO getByIdWithSpec(Long id);

    void updateWithSpec(ProductDTO productDTO);

    List<Product> list(Long categoryId);

    void startOrStop(Integer status, Long id);

    /**
     * 条件查询商品和规格
     */
    List<ProductVO> listWithSpec(Product product);
}
