package com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public class ErrorResponse extends OperationInfo {

    // ИД счета
    @JsonProperty
    private final String billId;

    // Состояние оплаты счета
    @JsonProperty("state")
    private String state = Statuses.FAIL_STATUS; // required

    // Состояние оплаты счета
    @JsonProperty
    protected final String substate;

    // Состояние оплаты счета
    @JsonProperty
    protected final String stackTraceMessage;

    public ErrorResponse(String rId, String substate, String billId, String stackTraceMessage) {
        super(rId);
        this.substate = substate;
        this.billId = billId;
        this.stackTraceMessage = stackTraceMessage;
    }
}
