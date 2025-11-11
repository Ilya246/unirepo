package ru.ssau.tk._AMEBA_._PESEZ_.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

import lombok.*;

@Entity
@Table(name = "Points")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PointsEntity {

    @EmbeddedId
    private PointId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "func_Id", insertable = false, updatable = false)
    private FunctionEntity function;

    @Column(name = "y_Value")
    private double yValue;


    public PointsEntity(FunctionEntity function, double xValue, double yValue) {
        this.id = new PointId(function.getFuncId(), xValue);
        this.function = function;
        this.yValue = yValue;
    }

    public String getPointAsString() {
        return "(" + id.getXValue() + ", " + yValue + ")";
    }

    public double distanceToOrigin() {
        return Math.sqrt(id.getXValue() * id.getXValue() + yValue * yValue);
    }

    // Геттеры для удобного доступа
    public double getXValue() {
        return id.getXValue();
    }

    public void setXValue(double xValue) {
        this.id.setXValue(xValue);
    }

    public Long getFunctionId() {
        return id.getFunctionId();
    }

    public void setFunctionId(Long functionId) {
        this.id.setFunctionId(functionId);
        // Если нужно обновить связанную сущность, нужно загрузить её отдельно
    }
}
