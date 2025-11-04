package ru.ssau.tk._AMEBA_._PESEZ_.dto;

import java.io.*;

public class OwnedFunctionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6522333532647672030L;

    public FunctionDTO function;
    public FunctionOwnershipDTO ownership;

    public OwnedFunctionDTO(FunctionDTO function, FunctionOwnershipDTO ownership) {
        this.function = function;
        this.ownership = ownership;
    }
}
