package ru.ssau.tk._AMEBA_._PESEZ_.dto;

public class OwnedFunctionDTO {
    public FunctionDTO function;
    public FunctionOwnershipDTO ownership;

    public OwnedFunctionDTO(FunctionDTO function, FunctionOwnershipDTO ownership) {
        this.function = function;
        this.ownership = ownership;
    }
}
