package ru.ssau.tk._AMEBA_._PESEZ_.entity;

import jakarta.persistence.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.CompositeFunction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Function")
public class FunctionEntity {

    @Id
    @Column(name = "func_Id")
    private int funcId;

    @Column(name = "type_Id", columnDefinition = "INT CHECK (type_Id >= 1 AND type_Id <= 3)")
    private int typeId;

    @Column(name = "expression", length = 200)
    private String expression;

    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FunctionOwnershipEntity> functionOwnerships = new ArrayList<>();

    // Новое поле - композитные функции, где эта функция является результирующей
    @OneToMany(mappedBy = "compositeFunction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompositeFunctionEntity> compositeFunctions = new ArrayList<>();

    // Функции, где эта функция используется как внутренняя
    @OneToMany(mappedBy = "innerFunction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompositeFunctionEntity> innerCompositeFunctions = new ArrayList<>();

    // Функции, где эта функция используется как внешняя
    @OneToMany(mappedBy = "outerFunction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompositeFunctionEntity> outerCompositeFunctions = new ArrayList<>();

    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PointsEntity> points = new ArrayList<>();

    // Constructors
    public FunctionEntity() {}

    public FunctionEntity(int funcId, int typeId, String expression) {
        this.funcId = funcId;
        setTypeId(typeId); // Используем сеттер для валидации
        this.expression = expression;
    }

    // Getters and Setters
    public int getFuncId() { return funcId; }
    public void setFuncId(int funcId) { this.funcId = funcId; }

    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) {
        if (typeId < 1 || typeId > 3) {
            throw new IllegalArgumentException("typeId must be between 1 and 3");
        }
        this.typeId = typeId;
    }


    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }

    public List<FunctionOwnershipEntity> getFunctionOwnerships() { return functionOwnerships; }
    public void setFunctionOwnerships(List<FunctionOwnershipEntity> functionOwnerships) { this.functionOwnerships = functionOwnerships; }

    public List<CompositeFunctionEntity> getInnerCompositeFunctions() { return innerCompositeFunctions; }
    public void setInnerCompositeFunctions(List<CompositeFunctionEntity> innerCompositeFunctions) { this.innerCompositeFunctions = innerCompositeFunctions; }

    public List<CompositeFunctionEntity> getOuterCompositeFunctions() { return outerCompositeFunctions; }
    public void setOuterCompositeFunctions(List<CompositeFunctionEntity> outerCompositeFunctions) { this.outerCompositeFunctions = outerCompositeFunctions; }

    public List<PointsEntity> getPoints() { return points; }
    public void setPoints(List<PointsEntity> points) { this.points = points; }

    // Дополнительные методы для удобства
    public boolean isValidType() {
        return typeId >= 1 && typeId <= 3;
    }

    @Override
    public String toString() {
        return "FunctionEntity{funcId=" + funcId + ", typeId=" + typeId + ", expression='" + expression + "'}";
    }
}
