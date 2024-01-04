package com.tiddev.authorization.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
public class ReloadEvent implements Serializable {
    private final String eventProducerId;
    private String clientId;
    private Long scopeId;
    private Long scopeListId;

    @Override
    public String toString() {
        return "ReloadEvent{" +
                "eventProducerId='" + eventProducerId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", scopeId=" + scopeId +
                ", scopeListId=" + scopeListId +
                '}';
    }

}
