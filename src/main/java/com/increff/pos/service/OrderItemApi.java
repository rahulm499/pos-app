package com.increff.pos.service;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class OrderItemApi {
    @Autowired
    private OrderItemDao dao;

    //validation checks need to be updated
    @Transactional(rollbackOn = ApiException.class)
    public void add(OrderItemPojo p) throws ApiException {
        dao.insert(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void delete(Integer id) {
        dao.delete(id);
    }


    @Transactional(rollbackOn  = ApiException.class)
    public void update(Integer id, OrderItemPojo p) throws ApiException {
        OrderItemPojo ex = getCheck(id);
        ex.setQuantity(p.getQuantity());
        ex.setSellingPrice(p.getSellingPrice());
    }

    @Transactional
    public OrderItemPojo getCheck(Integer id) throws ApiException {
        OrderItemPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("OrderItem with given ID does not exists, id: " + id);
        }
        return p;
    }
    @Transactional
    public List<OrderItemPojo> getByOrderId(Integer id) throws ApiException {
        List<OrderItemPojo> p = dao.selectByOrderId(id);
        if (p == null) {
            throw new ApiException("Order with given ID does not exists, id: " + id);
        }
        return p;
    }

    @Transactional
    public OrderItemPojo getByOrderProductId(Integer orderId, Integer productId) throws ApiException {
        OrderItemPojo p = dao.selectByOrderProductId(orderId, productId);
        return p;
    }



}
