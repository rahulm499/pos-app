package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="Inventory",
        uniqueConstraints ={@UniqueConstraint(columnNames = {"productId"})}

)
public class InventoryPojo {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "inventory_generator")
    @TableGenerator(name = "inventory_generator", table = "id_generator", pkColumnName = "id_key", valueColumnName = "id_value", pkColumnValue = "inventory_id", initialValue = 1000, allocationSize = 1)
    private Integer id;
    @Column(nullable = false, updatable = false)
    private Integer productId;
    @Column(nullable = false)
    private Integer quantity;
}
