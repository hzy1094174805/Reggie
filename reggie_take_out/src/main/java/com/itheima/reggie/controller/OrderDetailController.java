package com.itheima.reggie.controller;


import com.itheima.reggie.service.IOrderDetailService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 订单明细表 前端控制器
 * </p>
 *
 * @author ilovend
 * @since 2023-02-06
 */
@RestController
@RequestMapping("/orderDetail")
@Api(tags = "订单明细管理")
@Slf4j
public class OrderDetailController {
    @Autowired
    private IOrderDetailService orderDetailService;

}
