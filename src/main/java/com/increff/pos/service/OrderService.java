package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderDao dao;

    //validation checks need to be updated
    @Transactional(rollbackOn = ApiException.class)
    public void add(OrderPojo p) throws ApiException {
        dao.insert(p);
    }

    @Transactional
    public void delete(Integer id) {
        dao.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo get(Integer id) throws ApiException {
        return getCheck(id);
    }

    @Transactional
    public List<OrderPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(Integer id) throws ApiException {
        OrderPojo ex = getCheck(id);
        ex.setIsInvoiceGenerated(Boolean.TRUE);
        dao.update(ex);
    }

    @Transactional
    public OrderPojo getCheck(Integer id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Order with given ID does not exists, id: " + id);
        }
        return p;
    }
    @Transactional
    public List<OrderPojo> getOrderByDate(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
        List<OrderPojo> p = dao.selectByDate(startDate, endDate);
        return p;
    }

    //NORMALIZE NEEDS TO BE UPDATED
    protected static void normalize(OrderPojo p) {

    }
    protected void validate(OrderPojo p) throws ApiException{

    }

}
