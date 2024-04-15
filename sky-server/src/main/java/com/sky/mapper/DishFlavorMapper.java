package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 功能：
 * 作者：ljs
 * 日期：2024/4/15 0:47
 */
@Mapper
public interface   DishFlavorMapper {
    /**
     * 添加口味
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);
}