package com.kujudy.springbootmall.dao.impl;

import com.kujudy.springbootmall.dao.OrderDao;
import com.kujudy.springbootmall.dto.OrderQueryParams;
import com.kujudy.springbootmall.model.Order;
import com.kujudy.springbootmall.model.OrderItem;
import com.kujudy.springbootmall.rowmapper.OrderItemRowMapper;
import com.kujudy.springbootmall.rowmapper.OrderRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderDaoImpl implements OrderDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {
        String sql = "SELECT count(*) FROM `order` WHERE 1=1";
        Map<String, Object> params = new HashMap<>();
        sql = addFilteringSql(sql, params, orderQueryParams);
        Integer total = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return total;
    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {
        String sql = "SELECT order_id, user_id, total_amount, created_date, last_modified_date FROM `order` WHERE 1=1";
        Map<String, Object> params = new HashMap<>();
        sql = addFilteringSql(sql, params, orderQueryParams);
        sql = sql + " ORDER BY created_date DESC";

        // 注意这里的空格
        sql = sql + " LIMIT :limit OFFSET :offset";

        params.put("limit", orderQueryParams.getLimit());
        params.put("offset", orderQueryParams.getOffset());
        List<Order> orderList = namedParameterJdbcTemplate.query(sql, params, new OrderRowMapper());

        return orderList;
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        String sql = "SELECT oi.order_item_id, oi.order_id, oi.product_id, oi.quantity, " +
                "oi.amount, p.product_name, p.image_url " +
                "FROM order_item as oi " +
                "LEFT JOIN product as p ON oi.product_id = p.product_id " +
                "WHERE oi.order_id = :orderId";

        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);

        List<OrderItem> orderItemList = namedParameterJdbcTemplate.query(sql, params, new OrderItemRowMapper());

        return orderItemList;
    }

    @Override
    public Order getOrderById(Integer orderId) {
        String sql = "SELECT order_id, user_id, total_amount, created_date, last_modified_date " +
                "FROM `order` WHERE order_id = :orderId";
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);

        List<Order> orderList = namedParameterJdbcTemplate.query(sql, params, new OrderRowMapper());
        if(orderList.size()>0){
            return orderList.get(0);
        }else{
            return null;
        }
    }

    @Override
    public Integer createOrder(Integer userId, Integer totalAmount) {
        String sql = "INSERT INTO `order` (user_id, total_amount, created_date, last_modified_date) " +
                "VALUES (:userId, :totalAmount, :createdDate, :lastModifiedDate)";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("totalAmount", totalAmount);
        Date now = new Date();
        params.put("createdDate", now);
        params.put("lastModifiedDate", now);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        Integer orderId = keyHolder.getKey().intValue();
        return orderId;
    }

    @Override
    public void createOrderItems(Integer orderId, List<OrderItem> orderItemList) {

//        for (OrderItem orderItem: orderItemList) {
//
//            String sql = "INSERT INTO order_item (order_id, product_id, quantity, amount) " +
//                    "VALUES (:orderId, :productId, :quantity, :amount)";
//
//            Map<String, Object> params = new HashMap<>();
//            params.put("orderId", orderId);
//            params.put("productId", orderItem.getProductId());
//            params.put("quantity", orderItem.getQuantity());
//            params.put("amount", orderItem.getAmount());
//            namedParameterJdbcTemplate.update(sql, params);
//        }

        String sql = "INSERT INTO order_item (order_id, product_id, quantity, amount)" +
                "VALUES (:orderId, :productId, :quantity, :amount)";

        MapSqlParameterSource[] params = new MapSqlParameterSource[orderItemList.size()];
        for(int i = 0 ; i < orderItemList.size() ; i++){
            OrderItem orderItem = orderItemList.get(i);
            params[i] = new MapSqlParameterSource();
            params[i].addValue("orderId", orderId);
            params[i].addValue("productId", orderItem.getProductId());
            params[i].addValue("quantity", orderItem.getQuantity());
            params[i].addValue("amount", orderItem.getAmount());
        }
        namedParameterJdbcTemplate.batchUpdate(sql, params);
    }

    private String addFilteringSql(String sql, Map<String, Object> map, OrderQueryParams orderQueryParams) {
        if(orderQueryParams.getUserId()!=null){
            sql = sql + " AND user_id = :userId";
            map.put("userId", orderQueryParams.getUserId());
        }
        return sql;
    }
}
