package com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ToString
public class CheckWorkRequest extends OperationInfo {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    // ИД клиентского запроса
    @JsonProperty
    private final String dateTime;

    public CheckWorkRequest(String rID) {
        super(rID);
        this.dateTime = DATE_TIME_FORMATTER.format(LocalDateTime.now());
    }
}
