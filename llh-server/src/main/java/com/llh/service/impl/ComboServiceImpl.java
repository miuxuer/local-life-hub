package com.llh.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.llh.constant.MessageConstant;
import com.llh.constant.StatusConstant;
import com.llh.dto.ComboDTO;
import com.llh.dto.ComboPageQueryDTO;
import com.llh.entity.Product;
import com.llh.entity.Combo;
import com.llh.entity.ComboProduct;
import com.llh.exception.DeletionNotAllowedException;
import com.llh.exception.ComboEnableFailedException;
import com.llh.mapper.CategoryMapper;
import com.llh.mapper.ProductMapper;
import com.llh.mapper.ComboProductMapper;
import com.llh.mapper.ComboMapper;
import com.llh.result.PageResult;
import com.llh.service.ComboService;
import com.llh.vo.ProductItemVO;
import com.llh.vo.ComboVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComboServiceImpl implements ComboService {

    @Autowired
    private ComboMapper comboMapper;

    @Autowired
    private ComboProductMapper comboProductMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 新增套餐
     *
     * @param comboDTO
     */
    @Override
    @Transactional
    public void saveWithProduct(ComboDTO comboDTO) {
        //套餐表
        Combo combo = new Combo();
        BeanUtils.copyProperties(comboDTO, combo);
        comboMapper.insert(combo);

        //套餐与商品关系表
        Long comboId = combo.getId();

        List<ComboProduct> comboProducts = comboDTO.getComboProducts();
        if (comboProducts != null && !comboProducts.isEmpty()) {
            comboProducts.forEach(comboProduct -> comboProduct.setComboId(comboId));
            comboProductMapper.insertBatch(comboProducts);
        }

    }

    /**
     * 分页查询
     */
    @Override
    public PageResult pageQuery(ComboPageQueryDTO comboPageQueryDTO) {
        PageHelper.startPage(comboPageQueryDTO.getPage(), comboPageQueryDTO.getPageSize());

        Page<ComboVO> page = comboMapper.pageQuery(comboPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除套餐
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            Combo combo = comboMapper.getById(id);
            if (combo.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.COMBO_ON_SALE);
            }
        }

        //删除套餐表
        comboMapper.deleteByIds(ids);

        //删除套餐与商品关系表
        comboProductMapper.deleteByComboIds(ids);

    }

    /**
     * 根据id查询套餐
     */
    @Override
    public ComboVO getByIdWithProduct(Long id) {
        //查询套餐表
        Combo combo = comboMapper.getById(id);

        //查询套餐与商品关系表
        List<ComboProduct> comboProducts = comboProductMapper.getByComboId(id);

        ComboVO comboVO = new ComboVO();

        // TODO 这里可以加一个空指针异常判断 ，测试的时候查询不存在的id时，BeanUtils会报空指针异常

        BeanUtils.copyProperties(combo, comboVO);

        comboVO.setComboProducts(comboProducts);

        return comboVO;
    }

    /**
     * 修改套餐
     *
     * @param comboDTO
     */
    @Override
    @Transactional
    public void updateWithProduct(ComboDTO comboDTO) {
        Combo combo = new Combo();
        BeanUtils.copyProperties(comboDTO, combo);

        //修改套餐表
        comboMapper.update(combo);

        //修改套餐与商品关系表
        Long comboId = comboDTO.getId();
        List<ComboProduct> comboProducts = comboDTO.getComboProducts();

        //先删除全部关系商品
        comboProductMapper.deleteByComboId(comboId);

        //再添加多个商品
        if (comboProducts != null && !comboProducts.isEmpty()) {
            comboProducts.forEach(comboProduct -> comboProduct.setComboId(comboId));

            comboProductMapper.insertBatch(comboProducts);
        }

    }

    /**
     * 套餐起售、停售
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        //起售套餐时，判断套餐内是否有停售商品，有停售商品提示"套餐内包含未启售商品，无法启售
        List<Product> products = productMapper.getByComboId(id);
        if (products != null && !products.isEmpty()) {
            for (Product product : products) {
                if (product.getStatus() == StatusConstant.DISABLE) {
                    throw new ComboEnableFailedException(MessageConstant.COMBO_ENABLE_FAILED);
                }
            }
        }

        Combo combo = Combo.builder()
                .id(id)
                .status(status)
                .build();

        comboMapper.update(combo);

    }

    /**
     * 条件查询
     */
    public List<Combo> list(Combo combo) {
        List<Combo> list = comboMapper.list(combo);
        return list;
    }

    /**
     * 根据id查询商品选项
     */
    public List<ProductItemVO> getProductItemById(Long id) {
        return comboMapper.getProductItemByComboId(id);
    }

}
