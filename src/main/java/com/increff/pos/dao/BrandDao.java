package com.increff.pos.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.BrandPojo;

@Repository
public class BrandDao extends AbstractDao {
    private static String select_id = "select p from BrandPojo p where id=:id";
    private static String select_all = "select p from BrandPojo p";

    private static String select_brand_category = "select p from BrandPojo p where brand=:brand and category=:category";
    private static String select_category = "select p from BrandPojo p where category=:category";
    private static String select_brand = "select p from BrandPojo p where brand=:brand";

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(BrandPojo p) {
        em.persist(p);
    }

    public BrandPojo select(Integer id) {
        TypedQuery<BrandPojo> query = getQuery(select_id, BrandPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<BrandPojo> selectAll() {
        TypedQuery<BrandPojo> query = getQuery(select_all, BrandPojo.class);
        return query.getResultList();
    }

    public BrandPojo selectByBrandCategory(String brand, String category) {
        TypedQuery<BrandPojo> query = getQuery(select_brand_category, BrandPojo.class);
        query.setParameter("brand", brand);
        query.setParameter("category", category);
        return getSingle(query);
    }

    public List<BrandPojo> selectByBrand(String brand) {
        TypedQuery<BrandPojo> query = getQuery(select_brand, BrandPojo.class);
        query.setParameter("brand", brand);
        return query.getResultList();
    }

    public List<BrandPojo> selectByCategory(String category) {
        TypedQuery<BrandPojo> query = getQuery(select_category, BrandPojo.class);
        query.setParameter("category", category);
        return query.getResultList();
    }

}
