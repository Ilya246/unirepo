package ru.ssau.tk._AMEBA_._PESEZ_.exceptions;

public class IsInCompositeException extends RuntimeException {
    public int[] composites;

    public IsInCompositeException(String message, int[] composites) {
        super(message);
        this.composites = composites;
    }

    public IsInCompositeException(int[] composites) {
        this.composites = composites;
    }
}
