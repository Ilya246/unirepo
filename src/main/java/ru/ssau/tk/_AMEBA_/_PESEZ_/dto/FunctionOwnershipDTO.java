package ru.ssau.tk._AMEBA_._PESEZ_.dto;

import java.sql.Timestamp;

public class FunctionOwnershipDTO {
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
