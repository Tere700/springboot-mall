package com.kujudy.springbootmall.service.impl;

import com.kujudy.springbootmall.dao.OrderDao;
import com.kujudy.springbootmall.dao.ProductDao;
import com.kujudy.springbootmall.dto.BuyItem;
import com.kujudy.springbootmall.dto.CreateOrderRequest;
import com.kujudy.springbootmall.model.OrderItem;
import com.kujudy.springbootmall.model.Product;
import com.kujudy.springbootmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Transactional
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
        int totalAmount = 0;

        List<OrderItem> orderItemList = new ArrayList<>();

        for(BuyItem buyItem : createOrderRequest.getBuyItemList()){
            Product product = productDao.getProductById(buyItem.getProductId());

            //計算總價格
            int amount = buyItem.getQuantity() * product.getPrice();
            totalAmount += amount;
            //轉換buyItem to orderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(buyItem.getProductId());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);
            orderItemList.add(orderItem);
        }

        Integer orderId = orderDao.createOrder(userId , totalAmount);

        orderDao.createOrderItems(orderId, orderItemList);

        return orderId;
    }
}
