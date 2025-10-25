package ru.ssau.tk._AMEBA_._PESEZ_.entity;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

@Entity
@Table(name = "Function_Ownership")
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

    // Constructors
    public FunctionOwnershipEntity() {}

    public FunctionOwnershipEntity(UserEntity user, FunctionEntity function, Date createdDate, String funcName) {
        this.user = user;
        this.function = function;
        this.createdDate = createdDate;
        this.funcName = funcName;
        this.id = new FunctionOwnershipId(user.getUserId(), function.getFuncId());
    }

    // Getters and Setters
    public FunctionOwnershipId getId() { return id; }
    public void setId(FunctionOwnershipId id) { this.id = id; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public FunctionEntity getFunction() { return function; }
    public void setFunction(FunctionEntity function) { this.function = function; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public String getFuncName() { return funcName; }
    public void setFuncName(String funcName) { this.funcName = funcName; }
}
