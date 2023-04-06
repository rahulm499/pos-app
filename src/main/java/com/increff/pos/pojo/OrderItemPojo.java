package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="OrderItem",
        indexes = { @Index(name = "id", columnList = "id"),
                @Index(name = "productId", columnList = "productId"),
                @Index(name = "orderId", columnList = "orderId"),
        }

)
public class OrderItemPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_item_generator")
    @TableGenerator(name = "order_item_generator", table = "id_generator", pkColumnName = "id_key", valueColumnName = "id_value", pkColumnValue = "order_item_id", initialValue = 1000, allocationSize = 1)
    private Integer id;
    @Column(nullable = false)
    private Integer orderId;
    @Column(nullable = false)
    private Integer productId;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    private Double sellingPrice;
}

