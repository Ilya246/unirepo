package ru.ssau.tk._AMEBA_._PESEZ_.dto;

public class CompositeFunctionDTO {
    public final int funcId;
    public final int innerFuncId;
    public final int outerFuncId;

    public CompositeFunctionDTO(int funcId, int innerFuncId, int outerFuncId) {
        this.funcId = funcId;
        this.innerFuncId = innerFuncId;
        this.outerFuncId = outerFuncId;
    }
}
