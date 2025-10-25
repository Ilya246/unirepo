package ru.ssau.tk._AMEBA_._PESEZ_.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

@Entity
@Table(name = "Composite_Function")
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

    // Constructors
    public CompositeFunctionEntity() {}

    public CompositeFunctionEntity(FunctionEntity compositeFunction, FunctionEntity innerFunction, FunctionEntity outerFunction) {
        this.compositeFunction = compositeFunction;
        this.innerFunction = innerFunction;
        this.outerFunction = outerFunction;
    }

    // Getters and Setters
    public FunctionEntity getCompositeFunction() { return compositeFunction; }
    public void setCompositeFunction(FunctionEntity compositeFunction) { this.compositeFunction = compositeFunction; }

    public FunctionEntity getInnerFunction() { return innerFunction; }
    public void setInnerFunction(FunctionEntity innerFunction) { this.innerFunction = innerFunction; }

    public FunctionEntity getOuterFunction() { return outerFunction; }
    public void setOuterFunction(FunctionEntity outerFunction) { this.outerFunction = outerFunction; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositeFunctionEntity that = (CompositeFunctionEntity) o;
        return compositeFunction.equals(that.compositeFunction);
    }

    @Override
    public int hashCode() {
        return compositeFunction.hashCode();
    }}

