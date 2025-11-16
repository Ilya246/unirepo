package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

public class CompositeFunctionCreateRequest implements FunctionCreateRequest {
    public final int innerId;
    public final int outerId;

    public CompositeFunctionCreateRequest(int innerId, int outerId) {
        this.innerId = innerId;
        this.outerId = outerId;
    }
}
