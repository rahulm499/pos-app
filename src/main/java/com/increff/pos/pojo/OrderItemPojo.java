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
                @Index(name = "quantity", columnList = "quantity"),
                @Index(name = "orderId", columnList = "orderId"),
                @Index(name = "sellingPrice", columnList = "sellingPrice")
        }

)
public class OrderItemPojo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
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

