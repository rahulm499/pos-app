package com.increff.pos.service;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemDao dao;

    //validation checks need to be updated
    @Transactional(rollbackOn = ApiException.class)
    public void add(OrderItemPojo p) throws ApiException {
        dao.insert(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void delete(int id) {
        dao.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderItemPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional
    public List<OrderItemPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, OrderItemPojo p) throws ApiException {
        OrderItemPojo ex = getCheck(id);
        ex.setQuantity(p.getQuantity());
        ex.setSellingPrice(p.getSellingPrice());
        dao.update(ex);
    }

    @Transactional
    public OrderItemPojo getCheck(int id) throws ApiException {
        OrderItemPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("OrderItem with given ID does not exists, id: " + id);
        }
        return p;
    }
    @Transactional
    public List<OrderItemPojo> getByOrderId(int id) throws ApiException {
        List<OrderItemPojo> p = dao.selectByOrderId(id);
        if (p == null) {
            throw new ApiException("Order with given ID does not exists, id: " + id);
        }
        return p;
    }



}
