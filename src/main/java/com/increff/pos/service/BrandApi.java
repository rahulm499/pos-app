package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BrandApi {

	@Autowired
	private BrandDao dao;

	@Transactional(rollbackFor = ApiException.class)
	public void add(BrandPojo brandPojo) throws ApiException {
		validate(brandPojo);
		dao.insert(brandPojo);
	}

	@Transactional
	public BrandPojo get(Integer id) throws ApiException {
		return getCheck(id);
	}

	@Transactional
	public List<BrandPojo> getAll() {
		return dao.selectAll();
	}

	@Transactional(rollbackFor  = ApiException.class)
	public void update(Integer id, BrandPojo brandPojo) throws ApiException {
		BrandPojo ex = getCheck(id);
		if(ex.getBrand().equals(brandPojo.getBrand()) && ex.getCategory().equals(brandPojo.getCategory())){
			return;
		}
		validate(brandPojo);
		ex.setCategory(brandPojo.getCategory());
		ex.setBrand(brandPojo.getBrand());
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
		BrandPojo brandPojo = dao.selectByBrandCategory(brand, category);
		if(brandPojo == null){
			throw new ApiException("Brand Category Pair does not exists");
		}
		return brandPojo;
	}

	@Transactional
	public BrandPojo getByBrandCategory(String brand, String category) throws ApiException {
		return dao.selectByBrandCategory(brand, category);
	}

	@Transactional
	public List<BrandPojo> getCategory(String category) throws ApiException {
		return dao.selectByCategory(category);
	}
	@Transactional
	public List<BrandPojo> getBrand(String brand) throws ApiException {
		return dao.selectByBrand(brand);

	}

	protected void validate(BrandPojo brandPojo) throws ApiException {
		if(getByBrandCategory(brandPojo.getBrand(), brandPojo.getCategory())!=null){
			throw new ApiException("Brand Category Pair already exists");
		}
	}

}
