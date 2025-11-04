package ru.ssau.tk._AMEBA_._PESEZ_.dto;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

public class FunctionOwnershipDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5871923335808124847L;

    public final int userId;
    public final int funcId;
    public final Timestamp createdDate;
    public final String funcName;

    public FunctionOwnershipDTO(int userId, int funcId, Timestamp createdDate, String funcName) {
        this.userId = userId;
        this.funcId = funcId;
        this.createdDate = createdDate;
        this.funcName = funcName;
    }
}
