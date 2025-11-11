package ru.ssau.tk._AMEBA_._PESEZ_.entity;

import java.io.Serializable;
import java.util.Objects;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PointId implements Serializable {

    @Column(name = "func_Id")
    private Long functionId;  // переименовал для ясности

    @Column(name = "x_Value")
    private double xValue;


}
