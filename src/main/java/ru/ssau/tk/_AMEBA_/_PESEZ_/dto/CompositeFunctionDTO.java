package ru.ssau.tk._AMEBA_._PESEZ_.dto;

import java.io.Serial;
import java.io.Serializable;

public class CompositeFunctionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -1376499663154818864L;

    public final int funcId;
    public final int innerFuncId;
    public final int outerFuncId;

    public CompositeFunctionDTO(int funcId, int innerFuncId, int outerFuncId) {
        this.funcId = funcId;
        this.innerFuncId = innerFuncId;
        this.outerFuncId = outerFuncId;
    }
}
