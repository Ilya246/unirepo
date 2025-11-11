package ru.ssau.tk._AMEBA_._PESEZ_.entity;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;
import lombok.*;
@Entity
@Table(name = "Function_Ownership")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class FunctionOwnershipEntity {
    @EmbeddedId
    private FunctionOwnershipId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_Id")
    private UserEntity user;

    @ManyToOne
    @MapsId("funcId")
    @JoinColumn(name = "func_Id")
    private FunctionEntity function;

    @Column(name = "created_Date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "func_Name", length = 100)
    private String funcName;

    // Конструктор
    public FunctionOwnershipEntity(UserEntity user, FunctionEntity function, Date createdDate, String funcName) {
        this.user = user;
        this.function = function;
        this.createdDate = createdDate;
        this.funcName = funcName;
        this.id = new FunctionOwnershipId(user.getUserId(), function.getFuncId());
    }
}