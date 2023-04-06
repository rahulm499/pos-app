package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name="Orders",
        indexes = { @Index(name = "id", columnList = "id"),
                @Index(name = "created_at", columnList = "created_at"),
                @Index(name = "isInvoiceGenerated", columnList = "isInvoiceGenerated")
        }

)
public class OrderPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_generator")
    @TableGenerator(name = "order_generator", table = "id_generator", pkColumnName = "id_key", valueColumnName = "id_value", pkColumnValue = "order_id", initialValue = 1000, allocationSize = 1)
    private Integer id;
    @Column(nullable = false)
    private ZonedDateTime created_at;
    @Column(nullable = false)
    private Boolean isInvoiceGenerated;


}

