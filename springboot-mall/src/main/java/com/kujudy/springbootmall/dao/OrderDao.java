package com.kujudy.springbootmall.dao;

import com.kujudy.springbootmall.dto.OrderQueryParams;
import com.kujudy.springbootmall.model.Order;
import com.kujudy.springbootmall.model.OrderItem;

import java.util.List;

public interface OrderDao {

    Integer countOrder (OrderQueryParams orderQueryParams);

    List<Order> getOrders (OrderQueryParams orderQueryParams);

    List<OrderItem> getOrderItemsByOrderId(Integer orderId);

    Order getOrderById(Integer orderId);

    Integer createOrder(Integer userId, Integer totalAmount);

    void createOrderItems(Integer orderId, List<OrderItem> orderItemList);
}
