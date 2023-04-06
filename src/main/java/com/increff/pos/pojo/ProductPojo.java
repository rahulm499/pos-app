package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="Product",
        indexes = { @Index(name = "barcode", columnList = "barcode"),
                @Index(name = "brand_category", columnList = "brand_category")}

)
public class ProductPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "product_generator")
    @TableGenerator(name = "product_generator", table = "id_generator", pkColumnName = "id_key", valueColumnName = "id_value", pkColumnValue = "product_id", initialValue = 1000, allocationSize = 1)
    private Integer id;
    @Column(unique=true, nullable = false)
    private String barcode;
    @Column(nullable = false)
    private Integer brand_category;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double mrp;
}

