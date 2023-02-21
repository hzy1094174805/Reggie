package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.IOrdersService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author ilovend
 * @since 2023-02-06
 */
@RestController
@RequestMapping("/order")
@Api(tags = "订单管理")
@Slf4j
public class OrdersController {
    @Autowired
    private IOrdersService ordersService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单信息:{}", orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /*
       分页查询订单
    */
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> page(Integer page, Integer pageSize){
        Page<OrdersDto> pageInfo = ordersService.page(page, pageSize);
        return R.success(pageInfo);
    }
}
