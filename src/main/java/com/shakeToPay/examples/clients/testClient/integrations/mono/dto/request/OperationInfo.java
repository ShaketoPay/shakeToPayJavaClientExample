package com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public abstract class OperationInfo {

    // ИД клиентского запроса
    @JsonProperty
    private final String rID;
}
