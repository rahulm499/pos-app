package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="Inventory",
        indexes = { @Index(name = "id", columnList = "id"),
                @Index(name = "productId", columnList = "productId"),
                @Index(name = "quantity", columnList = "quantity")
        }

)
public class InventoryPojo {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @Column(unique=true, nullable = false, updatable = false)
    private Integer productId;
    @Column(nullable = false)
    private Integer quantity;
}
