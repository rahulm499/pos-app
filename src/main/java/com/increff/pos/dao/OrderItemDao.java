package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class OrderItemDao extends AbstractDao {
    private static String delete_id = "delete from OrderItemPojo p where id=:id";
    private static String select_id = "select p from OrderItemPojo p where id=:id";
    private static String select_all = "select p from OrderItemPojo p";
    private static String select_all_by_id = "select p from OrderItemPojo p where orderId=:orderId";
    private static String select_by_id_product = "select p from OrderItemPojo p where orderId=:orderId and productId=:productId";

    @Transactional
    public void insert(OrderItemPojo p) {
        em().persist(p);
    }

    @Transactional
    public int delete(int id) {
        Query query = em().createQuery(delete_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    public OrderItemPojo select(int id) {
        TypedQuery<OrderItemPojo> query = getQuery(select_id, OrderItemPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<OrderItemPojo> selectByOrderId(Integer id) {
        TypedQuery<OrderItemPojo> query = getQuery(select_all_by_id, OrderItemPojo.class);
        query.setParameter("orderId", id);
        return query.getResultList();
    }

    public OrderItemPojo selectByOrderProductId(Integer orderId, Integer productId) {
        TypedQuery<OrderItemPojo> query = getQuery(select_by_id_product, OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        query.setParameter("productId", productId);
        return getSingle(query);
    }

}
