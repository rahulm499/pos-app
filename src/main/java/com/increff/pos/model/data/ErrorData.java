package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErrorData{
    private Integer id;
    private List<Object> values;
    private String message;
}
