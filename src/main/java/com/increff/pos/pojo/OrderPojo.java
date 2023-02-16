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
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(name = "sequence-generator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "user_sequence"),
            @Parameter(name = "initial_value", value = "1001"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Integer id;
    @Column(nullable = false)
    private ZonedDateTime created_at;
    private Boolean isInvoiceGenerated;


}

