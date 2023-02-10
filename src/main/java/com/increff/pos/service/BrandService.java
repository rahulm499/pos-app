package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class BrandService {

	@Autowired
	private BrandDao dao;

	@Transactional(rollbackOn = ApiException.class)
	public void add(BrandPojo p) throws ApiException {
		dao.insert(p);
	}

	@Transactional(rollbackOn = ApiException.class)
	public void delete(Integer id) {
		dao.delete(id);
	}

	@Transactional(rollbackOn = ApiException.class)
	public BrandPojo get(Integer id) throws ApiException {
		return getCheck(id);
	}

	@Transactional(rollbackOn = ApiException.class)
	public List<BrandPojo> getAll() {
		return dao.selectAll();
	}

	@Transactional(rollbackOn  = ApiException.class)
	public void update(Integer id, BrandPojo p) throws ApiException {
		BrandPojo ex = getCheck(id);
		ex.setCategory(p.getCategory());
		ex.setBrand(p.getBrand());
		dao.update(ex);
	}

	@Transactional
	public BrandPojo getCheck(Integer id) throws ApiException {
		BrandPojo p = dao.select(id);
		if (p == null) {
			throw new ApiException("Brand with given ID does not exists, id: " + id);
		}
		return p;
	}
	@Transactional
	public BrandPojo getCheckBrandCategory(String brand, String category) throws ApiException {
		return dao.getByBrandCategory(brand, category);
	}


	@Transactional
	public List<BrandPojo> getCategory(String category) throws ApiException {
		return dao.getCategory(category);
	}
	@Transactional
	public List<BrandPojo> getBrand(String brand) throws ApiException {
		return dao.getBrand(brand);

	}

}
