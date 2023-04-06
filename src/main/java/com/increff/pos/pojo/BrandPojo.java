package com.increff.pos.pojo;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity

@Table(name="Brand",
        // We need indexes separately as unique constraints will create combined index for both columns
        uniqueConstraints ={@UniqueConstraint(columnNames = {"brand", "category"})}

)
public class BrandPojo {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "brand_generator")
    @TableGenerator(name = "brand_generator", table = "id_generator", pkColumnName = "id_key", valueColumnName = "id_value", pkColumnValue = "brand_id", initialValue = 1000, allocationSize = 1)
    private Integer id;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private String category;

}
