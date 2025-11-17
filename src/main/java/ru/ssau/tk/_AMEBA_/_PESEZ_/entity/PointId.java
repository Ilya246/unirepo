package ru.ssau.tk._AMEBA_._PESEZ_.entity;

import java.io.Serializable;
import java.util.Objects;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
        //@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PointId implements Serializable {

    @Column(name = "func_Id", nullable = false)
    private Long functionId;

    @Column(name = "x_Value", nullable = false)
    private Double xValue;

    public PointId(Long functionId, Double xValue) {
        Utility.Log.info("=== PointId constructor ===");
        Utility.Log.info("Input - functionId: {}, xValue: {}", functionId, xValue);

        this.functionId = functionId;
        this.xValue = xValue;

        Utility.Log.info("Result - functionId: {}, xValue: {}", this.functionId, this.xValue);
    }

}
