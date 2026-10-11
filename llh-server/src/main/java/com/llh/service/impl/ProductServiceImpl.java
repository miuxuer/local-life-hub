package com.llh.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.llh.constant.MessageConstant;
import com.llh.constant.StatusConstant;
import com.llh.dto.ProductDTO;
import com.llh.dto.ProductPageQueryDTO;
import com.llh.entity.Product;
import com.llh.entity.ProductSpec;
import com.llh.exception.DeletionNotAllowedException;
import com.llh.mapper.ProductSpecMapper;
import com.llh.mapper.ProductMapper;
import com.llh.mapper.ComboProductMapper;
import com.llh.result.PageResult;
import com.llh.service.ProductSearchService;
import com.llh.service.ProductService;
import com.llh.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSpecMapper productSpecMapper;

    @Autowired
    private ComboProductMapper comboProductMapper;

    @Autowired
    private ProductSearchService productSearchService;

    /**
     * 新增商品
     *
     * @param productDTO
     */
    @Override
    @Transactional //修改多张表，需要事务管理，要么全部成功，要么全部失败
    public void saveWithSpec(ProductDTO productDTO) {
        //添加商品
        Product product = new Product();

        BeanUtils.copyProperties(productDTO, product);

        productMapper.insert(product);

        //添加规格

        //取回id
        Long id = product.getId();
        //拿到规格集合
        List<ProductSpec> specs = productDTO.getSpecs();

        if (specs != null && !specs.isEmpty()) {
            //往规格集合添加id
            specs.forEach(productSpec -> productSpec.setProductId(id));

            productSpecMapper.insertBatch(specs);
        }

        //同步商品索引
        productSearchService.syncProduct(product);

    }

    /**
     * 商品分页查询
     *
     * @param productPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(ProductPageQueryDTO productPageQueryDTO) {
        PageHelper.startPage(productPageQueryDTO.getPage(), productPageQueryDTO.getPageSize());

        Page<ProductVO> page = productMapper.pageQuery(productPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除商品
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断商品是否在售
        for (Long id : ids) {
            Product product = productMapper.getById(id);
            //TODO 这里可以添加一条非空判断，如果删除的商品不存在时，会报错（只会在测试中出现，因为前端展示出来的商品都是从数据库中查到真实存在的）

            if (product.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.PRODUCT_ON_SALE);
            }
        }

        //判断商品是否属于套餐
        List<Long> comboId = comboProductMapper.getComboIdsByProductIds(ids);
        if (comboId != null && !comboId.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_PRODUCT);
        }

        //删除商品
        productMapper.deleteByIds(ids);

        //删除商品规格
        productSpecMapper.deleteByProductIds(ids);

        //删除商品索引
        productSearchService.deleteByIds(ids);

    }

    /**
     * 根据id查询商品
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public ProductVO getByIdWithSpec(Long id) {
        //查询商品
        Product product = productMapper.getById(id);

        //查询规格
        List<ProductSpec> productSpecs = productSpecMapper.getByProductId(id);

        //封装到一个VO对象中
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product, productVO);
        productVO.setSpecs(productSpecs);

        return productVO;
    }

    /**
     * 修改商品
     *
     */
    @Override
    public void updateWithSpec(ProductDTO productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);

        //修改商品
        productMapper.update(product);

        //修改规格
        Long productDTOId = productDTO.getId();
        //先删除所有规格
        productSpecMapper.deleteByProductId(productDTOId);

        //再添加规格
        List<ProductSpec> specs = productDTO.getSpecs();
        if (specs != null && !specs.isEmpty()) {
            specs.forEach(productSpec -> productSpec.setProductId(productDTOId));

            productSpecMapper.insertBatch(specs);
        }

        //同步商品索引
        productSearchService.syncProduct(product);

    }

    /**
     * 根据分类id查询商品
     */
    @Override
    public List<Product> list(Long categoryId) {
        Product product = Product.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)  //只查起售的商品
                .build();
        return productMapper.list(product);
    }

    /**
     * 商品起售、停售
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Product product = Product.builder()
                .id(id)
                .status(status)
                .build();
        productMapper.update(product);

        //同步商品索引，这里只改了状态，需要重新查询完整的商品
        productSearchService.syncProduct(productMapper.getById(id));

    }

    /**
     * 条件查询商品和规格
     */
    public List<ProductVO> listWithSpec(Product product) {
        List<Product> productList = productMapper.list(product);

        List<ProductVO> productVOList = new ArrayList<>();

        for (Product d : productList) {
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(d,productVO);

            //根据商品id查询对应的规格
            List<ProductSpec> specs = productSpecMapper.getByProductId(d.getId());

            productVO.setSpecs(specs);
            productVOList.add(productVO);
        }

        return productVOList;
    }
}
