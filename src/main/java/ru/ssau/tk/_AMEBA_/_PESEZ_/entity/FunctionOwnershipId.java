package ru.ssau.tk._AMEBA_._PESEZ_.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import lombok.*;
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FunctionOwnershipId implements Serializable {
    @Column(name = "user_Id")
    private Long userId;

    @Column(name = "func_Id")
    private Long funcId;
}
