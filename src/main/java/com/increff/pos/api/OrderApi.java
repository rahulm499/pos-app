package com.increff.pos.api;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class OrderApi {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;

    //validation checks need to be updated
    @Transactional
    public void add(OrderPojo p){
        orderDao.insert(p);
    }


    public OrderPojo get(Integer id){
        return orderDao.select(id);
    }

    public List<OrderPojo> getAll() {
        return orderDao.selectAll();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateInvoice(Integer id) throws ApiException {
        OrderPojo ex = getCheck(id);
        ex.setIsInvoiceGenerated(Boolean.TRUE);
    }

    public OrderPojo getCheck(Integer id) throws ApiException {
        OrderPojo p = orderDao.select(id);
        if (p == null) {
            throw new ApiException("Order with given ID does not exists, id: " + id);
        }
        return p;
    }

    public List<OrderPojo> getOrderByDate(ZonedDateTime startDate, ZonedDateTime endDate) {
        return orderDao.selectByDate(startDate, endDate);
    }

    // Order Item Api

    @Transactional
    public void addOrderItem(OrderItemPojo p){
        orderItemDao.insert(p);
    }

    @Transactional
    public void deleteOrderItem(Integer id) {
        orderItemDao.delete(id);
    }


    @Transactional(rollbackOn  = ApiException.class)
    public void update(Integer id, OrderItemPojo p) throws ApiException {
        OrderItemPojo ex = getCheckByItemId(id);
        ex.setQuantity(p.getQuantity());
        ex.setSellingPrice(p.getSellingPrice());
    }


    public OrderItemPojo getCheckByItemId(Integer id) throws ApiException {
        OrderItemPojo p = orderItemDao.select(id);
        if (p == null) {
            throw new ApiException("OrderItem with given ID does not exists, id: " + id);
        }
        return p;
    }
    public List<OrderItemPojo> getByOrderItemId(Integer id) throws ApiException {
        List<OrderItemPojo> p = orderItemDao.selectByOrderId(id);
        if (p == null) {
            throw new ApiException("Order with given ID does not exists, id: " + id);
        }
        return p;
    }

    public OrderItemPojo getByOrderItemProductId(Integer orderId, Integer productId){
        return orderItemDao.selectByOrderProductId(orderId, productId);
    }

}
