package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="Product",
        indexes = { @Index(name = "id", columnList = "id"),
                @Index(name = "barcode", columnList = "barcode"),
                @Index(name = "brand_category", columnList = "brand_category"),
                @Index(name = "name", columnList = "name"),
                @Index(name = "mrp", columnList = "mrp")}

)
public class ProductPojo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
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

