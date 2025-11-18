package ru.ssau.tk._AMEBA_._PESEZ_.enums;

public enum UserType {
    Normal(1),
    Admin(2);

    public final int typeId;
    UserType(int type) {
        this.typeId = type;
    }

    public static UserType fromId(int from) {
        return switch(from) {
            case 1 -> Normal;
            case 2 -> Admin;
            default -> throw new IllegalArgumentException();
        };
    }
}
