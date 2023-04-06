package com.increff.pos.api;

import com.increff.pos.dao.DailyReportDao;
import com.increff.pos.pojo.DailyReportPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.List;

@Service
public class DailyReportApi {
    @Autowired
    private DailyReportDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void add(DailyReportPojo p){
        dao.insert(p);
    }

    public List<DailyReportPojo> getAll(){
        return dao.selectAll();
    }

}
