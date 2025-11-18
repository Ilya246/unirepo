package ru.ssau.tk._AMEBA_._PESEZ_.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.CompositeFunction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Function")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class FunctionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "func_Id")
    private Long funcId;

    @Column(name = "type_Id", columnDefinition = "INT CHECK (type_Id >= 1 AND type_Id <= 3)")
    private int typeId;

    @Column(name = "expression", length = 200)
    private String expression;

    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FunctionOwnershipEntity> functionOwnerships = new ArrayList<>();

    @OneToMany(mappedBy = "compositeFunction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompositeFunctionEntity> compositeFunctions = new ArrayList<>();

    @OneToMany(mappedBy = "innerFunction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompositeFunctionEntity> innerCompositeFunctions = new ArrayList<>();

    @OneToMany(mappedBy = "outerFunction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompositeFunctionEntity> outerCompositeFunctions = new ArrayList<>();

    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PointsEntity> points = new ArrayList<>();

    // Конструктор
    public FunctionEntity(int typeId, String expression) {
        setTypeId(typeId);
        this.expression = expression;
    }

    // Кастомный сеттер
    public void setTypeId(int typeId) {
        if (typeId < 1 || typeId > 3) {
            throw new IllegalArgumentException("typeId must be between 1 and 3");
        }
        this.typeId = typeId;
    }

    public boolean isValidType() {
        return typeId >= 1 && typeId <= 3;
    }
}
