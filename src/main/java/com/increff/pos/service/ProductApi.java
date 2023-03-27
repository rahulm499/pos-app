package com.increff.pos.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductApi {
    @Autowired
    private ProductDao dao;

    //validation checks need to be updated
    @Transactional(rollbackFor = ApiException.class)
    public void add(ProductPojo p) throws ApiException {
       dao.insert(p);
    }

    @Transactional(rollbackFor = ApiException.class)
    public ProductPojo get(Integer id) throws ApiException {
        return getCheck(id);
    }

    @Transactional
    public List<ProductPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackFor  = ApiException.class)
    public void update(Integer id, ProductPojo p) throws ApiException {
        ProductPojo ex = getCheck(id);
        ex.setName(p.getName());
        ex.setMrp(p.getMrp());
    }

    @Transactional
    public ProductPojo getCheck(Integer id) throws ApiException {
        ProductPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Product with given ID does not exists, id: " + id);
        }
        return p;
    }
    @Transactional
    public ProductPojo getCheckBarcode(String barcode) throws ApiException {
        ProductPojo p = dao.selectByBarcode(barcode);
        if (p == null) {
            throw new ApiException("Product with given barcode does not exists, Barcode: " + barcode);
        }
        return p;
    }

    public void validateBarcode(String barcode) throws ApiException{
        if(dao.selectByBarcode(barcode)!=null){
            throw new ApiException("Barcode must be unique");
        }
    }

    @Transactional
    public  List<ProductPojo> getBrandCategory(Integer brand_category) throws ApiException {
        List<ProductPojo> p = dao.selectByBrandCategory(brand_category);
        if (p == null) {
            throw new ApiException("Product with given Brand Category does not exists, brand_category: " + brand_category);
        }
        return p;
    }


}
