package ru.ssau.tk._AMEBA_._PESEZ_.dto.response;

public class CompositeFunctionResponse {
    public int innerId;
    public int outerId;

    public CompositeFunctionResponse(int outerId, int innerId) {
        this.outerId = outerId;
        this.innerId = innerId;
    }
}
