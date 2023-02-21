package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrdersMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 订单服务impl
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author ilovend
 * @date 2023/02/18
 * @since 2023-02-06
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {
    @Autowired
    private IShoppingCartService shoppingCartService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IAddressBookService addressBookService;
    @Autowired
    private IOrderDetailService orderDetailService;

    /**
     * 用户下单
     *
     * @param orders 订单
     */
    @Override
    public void submit(Orders orders) {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("购物车为空，不能下单");
        }

        User user = userService.getById(userId);

        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("用户地址信息有误，不能下单");
        }
        long orderId = IdWorker.getId();

        AtomicInteger amount = new AtomicInteger(0);
        //组装订单明细信息
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        //组装订单数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        this.save(orders);
        orderDetailService.saveBatch(orderDetails);
        shoppingCartService.remove(wrapper);
    }

    /*
        分页查询订单和明细
     */
    @Override
    public Page<OrdersDto> page(Integer page, Integer pageSize) {
        //分页条件
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        //当前用户条件
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getUserId,BaseContext.get());
        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        super.page(pageInfo,ordersLambdaQueryWrapper);
        Page<OrdersDto> pageDtoInfo = new Page<>(page, pageSize);
        //把订单分页数据转成Dto分页数据
        BeanUtils.copyProperties(pageInfo,pageDtoInfo,"records");
        //手动设置records
        List<OrdersDto> ordersDtoList = pageInfo.getRecords().stream().map(orders -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders, ordersDto);
            //缺少订单明细
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            //select * from orderDetail  where order_id=?
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, orders.getId());
            List<OrderDetail> list = orderDetailService.list(orderDetailLambdaQueryWrapper);
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());
        pageDtoInfo.setRecords(ordersDtoList);
        return pageDtoInfo;
    }
}
