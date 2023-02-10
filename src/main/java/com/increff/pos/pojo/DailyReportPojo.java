package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name="DailyReport",
        indexes = { @Index(name = "id", columnList = "id"),
                @Index(name = "date", columnList = "date"),
                @Index(name = "invoiced_orders_count", columnList = "invoiced_orders_count"),
                @Index(name = "invoiced_items_count", columnList = "invoiced_items_count"),
                @Index(name = "total_revenue", columnList = "total_revenue") }

)
public class DailyReportPojo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private ZonedDateTime date;
    private Integer invoiced_orders_count;
    private Integer invoiced_items_count;
    private Double total_revenue;


}

