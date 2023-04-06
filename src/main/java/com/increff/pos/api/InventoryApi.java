package com.increff.pos.api;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class InventoryApi {
    @Autowired
    private InventoryDao dao;

    //validation checks need to be updated
    @Transactional
    public void add(InventoryPojo p){
        dao.insert(p);
    }

    public InventoryPojo get(Integer id){
        return dao.select(id);
    }

    public List<InventoryPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(Integer id, InventoryPojo p) throws ApiException {
        InventoryPojo ex = getCheck(id);
        ex.setQuantity(p.getQuantity());
    }

    public InventoryPojo getCheck(Integer id) throws ApiException {
        InventoryPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Inventory with given ID does not exists, id: " + id);
        }
        return p;
    }

    public InventoryPojo getByProduct(Integer id) {
        return dao.selectByProductId(id);
    }


}
