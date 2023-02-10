package com.increff.pos.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.EmployeePojo;

@Repository
public class EmployeeDao extends AbstractDao {

	private static String delete_id = "delete from EmployeePojo p where id=:id";
	private static String select_id = "select p from EmployeePojo p where id=:id";
	private static String select_all = "select p from EmployeePojo p";

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void insert(EmployeePojo p) {
		em.persist(p);
	}

	public int delete(int id) {
		Query query = em.createQuery(delete_id);
		query.setParameter("id", id);
		return query.executeUpdate();
	}

	public EmployeePojo select(int id) {
		TypedQuery<EmployeePojo> query = getQuery(select_id, EmployeePojo.class);
		query.setParameter("id", id);
		return getSingle(query);
	}

	public List<EmployeePojo> selectAll() {
		TypedQuery<EmployeePojo> query = getQuery(select_all, EmployeePojo.class);
		return query.getResultList();
	}

	public void update(EmployeePojo p) {
	}



}
