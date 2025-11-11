package ru.ssau.tk._AMEBA_._PESEZ_.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import lombok.*;
@Entity
@Table(name = "Composite_Function")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CompositeFunctionEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "func_Id")
    private FunctionEntity compositeFunction;

    @ManyToOne
    @JoinColumn(name = "inner_Func_Id")
    private FunctionEntity innerFunction;

    @ManyToOne
    @JoinColumn(name = "outer_Func_Id")
    private FunctionEntity outerFunction;
}