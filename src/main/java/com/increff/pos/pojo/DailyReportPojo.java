package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name="DailyReport"
)
public class DailyReportPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "daily_report_generator")
    @TableGenerator(name = "daily_report_generator", table = "id_generator", pkColumnName = "id_key", valueColumnName = "id_value", pkColumnValue = "daily_report_id", initialValue = 1000, allocationSize = 1)
    private Integer id;

    @Column(nullable = false, unique = true)
    private ZonedDateTime date;
    @Column(nullable = false)
    private Integer invoiced_orders_count;
    @Column(nullable = false)
    private Integer invoiced_items_count;
    @Column(nullable = false)
    private Double total_revenue;


}

