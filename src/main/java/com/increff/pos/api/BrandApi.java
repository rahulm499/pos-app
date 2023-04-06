package com.increff.pos.api;

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

	public BrandPojo get(Integer id){
		return dao.select(id);
	}

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

	public BrandPojo getCheck(Integer id) throws ApiException {
		BrandPojo p = dao.select(id);
		if (p == null) {
			throw new ApiException("Brand with given ID does not exists, id: " + id);
		}
		return p;
	}
	public BrandPojo getCheckBrandCategory(String brand, String category) throws ApiException {
		BrandPojo brandPojo = dao.selectByBrandCategory(brand, category);
		if(brandPojo == null){
			throw new ApiException("Brand Category Pair does not exists");
		}
		return brandPojo;
	}

	public BrandPojo getByBrandCategory(String brand, String category){
		return dao.selectByBrandCategory(brand, category);
	}

	public List<BrandPojo> getCategory(String category){
		return dao.selectByCategory(category);
	}
	public List<BrandPojo> getBrand(String brand) {
		return dao.selectByBrand(brand);

	}

	protected void validate(BrandPojo brandPojo) throws ApiException {
		if(getByBrandCategory(brandPojo.getBrand(), brandPojo.getCategory())!=null){
			throw new ApiException("Brand Category Pair already exists");
		}
	}

}
