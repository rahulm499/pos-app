package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class InventoryDao extends AbstractDao {
    private static String select_id = "select p from InventoryPojo p where id=:id";
    private static String select_by_prod_id = "select p from InventoryPojo p where productId=:id";
    private static String select_all = "select p from InventoryPojo p";

    @Transactional
    public void insert(InventoryPojo p) {
        em().persist(p);
    }

    public InventoryPojo select(Integer id) {
        TypedQuery<InventoryPojo> query = getQuery(select_id, InventoryPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public InventoryPojo selectByProductId(Integer id) {
        TypedQuery<InventoryPojo> query = getQuery(select_by_prod_id, InventoryPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<InventoryPojo> selectAll() {
        TypedQuery<InventoryPojo> query = getQuery(select_all, InventoryPojo.class);
        return query.getResultList();
    }


}
