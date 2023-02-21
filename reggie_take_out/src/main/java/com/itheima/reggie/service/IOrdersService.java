package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.Orders;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author ilovend
 * @since 2023-02-06
 */
public interface IOrdersService extends IService<Orders> {

    void submit(Orders orders);

    Page<OrdersDto> page(Integer page, Integer pageSize);
}
