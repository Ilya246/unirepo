package ru.ssau.tk._AMEBA_._PESEZ_.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

import lombok.*;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility;

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
    @MapsId("functionId")
    @JoinColumn(name = "func_Id", insertable = false, updatable = false)
    private FunctionEntity function;

    @Column(name = "y_Value")
    private Double yValue;


    public PointsEntity(FunctionEntity function, Double xValue, Double yValue) {
        Utility.Log.info("=== PointsEntity constructor ===");
        Utility.Log.info("Input - functionId: {}, xValue: {}, yValue: {}",
                function.getFuncId(), xValue, yValue);

        this.id = new PointId(function.getFuncId(), xValue);
        this.function = function;
        this.yValue = yValue;

        Utility.Log.info("Result - id.functionId: {}, id.xValue: {}, yValue: {}",
                this.id.getFunctionId(), this.id.getXValue(), this.yValue);
    }

    public String getPointAsString() {
        return "(" + id.getXValue() + ", " + yValue + ")";
    }

    public Double distanceToOrigin() {
        return Math.sqrt(id.getXValue() * id.getXValue() + yValue * yValue);
    }

    // Геттеры для удобного доступа
    public Double getXValue() {
        return id.getXValue();
    }

    public void setXValue(Double xValue) {
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
