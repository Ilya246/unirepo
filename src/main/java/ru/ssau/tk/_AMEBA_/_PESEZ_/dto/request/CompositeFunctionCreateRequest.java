package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import java.io.Serial;
import java.io.Serializable;

public class CompositeFunctionCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 6285582588036227474L;

    public final int innerId;
    public final int outerId;

    public CompositeFunctionCreateRequest(int innerId, int outerId) {
        this.innerId = innerId;
        this.outerId = outerId;
    }
}
