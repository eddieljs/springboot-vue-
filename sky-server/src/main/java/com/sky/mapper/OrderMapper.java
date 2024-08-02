package com.sky.mapper;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 功能：
 * 作者：ljs
 * 日期：2024/8/1 15:24
 */
@Mapper
public interface OrderMapper {
    /**
     * 创建订单
     * @param orders
     */
    void insert(Orders orders);


}