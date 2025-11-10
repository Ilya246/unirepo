package ru.ssau.tk._AMEBA_._PESEZ_.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FunctionOwnershipId implements Serializable {

    @Column(name = "user_Id")
    private Long userId;

    @Column(name = "func_Id")
    private Long funcId;

    public FunctionOwnershipId() {}

    public FunctionOwnershipId(Long userId, Long funcId) {
        this.userId = userId;
        this.funcId = funcId;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getFuncId() { return funcId; }
    public void setFuncId(Long funcId) { this.funcId = funcId; }

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
