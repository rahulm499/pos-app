package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class InventoryService {
    @Autowired
    private InventoryDao dao;

    //validation checks need to be updated
    @Transactional(rollbackOn = ApiException.class)
    public void add(InventoryPojo p) throws ApiException {
        dao.insert(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void delete(Integer id) {
        dao.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo get(Integer id) throws ApiException {
        return getCheck(id);
    }

    @Transactional
    public List<InventoryPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(Integer id, InventoryPojo p) throws ApiException {
        InventoryPojo ex = getCheck(id);
        ex.setQuantity(p.getQuantity());
        dao.update(ex);
    }

    @Transactional
    public InventoryPojo getCheck(Integer id) throws ApiException {
        InventoryPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Inventory with given ID does not exists, id: " + id);
        }
        return p;
    }

    @Transactional
    public InventoryPojo getByProduct(Integer id) throws ApiException {
        InventoryPojo p = dao.selectByProductId(id);
        return p;
    }



}
