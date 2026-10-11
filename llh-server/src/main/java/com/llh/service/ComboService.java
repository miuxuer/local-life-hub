package com.llh.service;

import com.llh.dto.ComboDTO;
import com.llh.dto.ComboPageQueryDTO;
import com.llh.entity.Combo;
import com.llh.result.PageResult;
import com.llh.vo.ProductItemVO;
import com.llh.vo.ComboVO;

import java.util.List;

public interface ComboService {
    void saveWithProduct(ComboDTO comboDTO);

    PageResult pageQuery(ComboPageQueryDTO comboPageQueryDTO);

    void deleteBatch(List<Long> ids);

    ComboVO getByIdWithProduct(Long id);

    void updateWithProduct(ComboDTO comboDTO);

    void startOrStop(Integer status, Long id);

    /**
     * 条件查询
     */
    List<Combo> list(Combo combo);

    /**
     * 根据id查询商品选项
     */
    List<ProductItemVO> getProductItemById(Long id);
}
