package ru.ssau.tk._AMEBA_._PESEZ_.enums;

import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;

public enum FunctionType {
    math(1),
    tabulated(2),
    composite(3),
    pure(4);

    int typeId;
    FunctionType(int type) {
        typeId = type;
    }

    public static FunctionType fromEntity(FunctionEntity ent) {
        int typeId = ent.getTypeId();
        return switch(typeId) {
            case 1 -> math;
            case 2 -> tabulated;
            case 3 -> composite;
            default -> pure;
        };
    }
}
