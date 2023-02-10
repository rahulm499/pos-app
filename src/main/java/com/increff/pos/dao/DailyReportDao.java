package com.increff.pos.dao;

import com.increff.pos.pojo.DailyReportPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class DailyReportDao extends AbstractDao {

	private static String delete_id = "delete from DailyReportPojo p where id=:id";
	private static String select_id = "select p from DailyReportPojo p where id=:id";
	private static String select_all = "select p from DailyReportPojo p";

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void insert(DailyReportPojo p) {
		em.persist(p);
	}

	public Integer delete(Integer id) {
		Query query = em.createQuery(delete_id);
		query.setParameter("id", id);
		return query.executeUpdate();
	}

	public DailyReportPojo select(Integer id) {
		TypedQuery<DailyReportPojo> query = getQuery(select_id, DailyReportPojo.class);
		query.setParameter("id", id);
		return getSingle(query);
	}

	public List<DailyReportPojo> selectAll() {
		TypedQuery<DailyReportPojo> query = getQuery(select_all, DailyReportPojo.class);
		return query.getResultList();
	}

	public void update(DailyReportPojo p) {
	}



}
