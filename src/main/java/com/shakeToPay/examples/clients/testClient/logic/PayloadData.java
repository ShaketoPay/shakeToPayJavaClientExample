package com.shakeToPay.examples.clients.testClient.logic;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PayloadData {

    @JsonProperty("rID")
    private String rID;

    @JsonProperty
    private String operation;

    @JsonProperty
    private String tableNumber;

    @JsonProperty
    private String billId;

    @JsonProperty
    private Double sum;

    @JsonProperty
    private Boolean prepay;
}
