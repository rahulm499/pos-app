package com.increff.pos.util.helper;

import com.increff.pos.model.data.BrandReportData;
import com.increff.pos.model.data.DailyReportData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.DailyReportPojo;

import java.time.ZoneId;

public class ReportsHelperUtil {
    public static BrandReportData convertBrandReport(BrandPojo p){
        BrandReportData d =new BrandReportData();
        d.setBrand(p.getBrand());
        d.setCategory(p.getCategory());
        d.setId(p.getId());
        return d;
    }

    public static DailyReportData convertDailyReport(DailyReportPojo p){
        DailyReportData d = new DailyReportData();
        d.setDate(String.valueOf(p.getDate().withZoneSameInstant(ZoneId.of("UTC") )));
        d.setTotal_revenue(p.getTotal_revenue());
        d.setInvoiced_items_count(p.getInvoiced_items_count());
        d.setInvoiced_orders_count(p.getInvoiced_orders_count());
        return d;
    }
}
