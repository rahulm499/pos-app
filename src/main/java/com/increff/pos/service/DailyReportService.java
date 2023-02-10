package com.increff.pos.service;

import com.increff.pos.dao.DailyReportDao;
import com.increff.pos.pojo.DailyReportPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.List;

@Service
public class DailyReportService {
    @Autowired
    private DailyReportDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void add(DailyReportPojo p){
        dao.insert(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public DailyReportPojo get(Integer id){
        return dao.select(id);
    }

    @Transactional
    public void delete(Integer id) {
        dao.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<DailyReportPojo> getAll(){
        return dao.selectAll();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(Integer id, DailyReportPojo p) throws ApiException {

        DailyReportPojo ex = getCheck(id);
        dao.update(ex);
    }
    @Transactional
    public DailyReportPojo getCheck(Integer id) throws ApiException {
        DailyReportPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Report with given ID does not exists, id: " + id);
        }
        return p;
    }

}
