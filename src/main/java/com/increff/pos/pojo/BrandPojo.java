package com.increff.pos.pojo;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity

@Table(name="Brand",
        indexes = { @Index(columnList = "brand"),
                    @Index(columnList = "category")},
        uniqueConstraints ={@UniqueConstraint(columnNames = {"brand", "category"})}

)
public class BrandPojo {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private String category;

}
