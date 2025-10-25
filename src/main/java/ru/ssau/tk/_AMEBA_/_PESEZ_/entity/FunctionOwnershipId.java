package ru.ssau.tk._AMEBA_._PESEZ_.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FunctionOwnershipId implements Serializable {

    @Column(name = "user_Id")
    private int userId;

    @Column(name = "func_Id")
    private int funcId;

    public FunctionOwnershipId() {}

    public FunctionOwnershipId(int userId, int funcId) {
        this.userId = userId;
        this.funcId = funcId;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getFuncId() { return funcId; }
    public void setFuncId(int funcId) { this.funcId = funcId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionOwnershipId that = (FunctionOwnershipId) o;
        return userId == that.userId && funcId == that.funcId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, funcId);
    }
}
