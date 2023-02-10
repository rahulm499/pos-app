package com.increff.pos.dao;

import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class OrderDao extends AbstractDao{
    private static String delete_id = "delete from OrderPojo p where id=:id";
    private static String select_id = "select p from OrderPojo p where id=:id";
    private static String select_all = "select p from OrderPojo p";
    private static String select_by_date = "select p from OrderPojo p where p.created_at >=:startDate and p.created_at<=:endDate";

    @Transactional
    public void insert(OrderPojo p) {
        em().persist(p);
    }

    public Integer delete(Integer id) {
        Query query = em().createQuery(delete_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    public OrderPojo select(Integer id) {
        TypedQuery<OrderPojo> query = getQuery(select_id, OrderPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<OrderPojo> selectAll() {
        TypedQuery<OrderPojo> query = getQuery(select_all, OrderPojo.class);
        return query.getResultList();
    }
    public List<OrderPojo> selectByDate(ZonedDateTime startDate, ZonedDateTime endDate){
        TypedQuery<OrderPojo> query = getQuery(select_by_date, OrderPojo.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    public void update(OrderPojo p) {
    }


}
