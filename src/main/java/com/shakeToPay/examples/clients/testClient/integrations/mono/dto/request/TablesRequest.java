package com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
public class TablesRequest extends OperationInfo {

    // инфо по столикам
    @JsonProperty("tables")
    public List<TableInfo> tables;

    public TablesRequest(String rID, List<TableInfo> tables) {
        super(rID);
        this.tables = tables;
    }

    @ToString
    @AllArgsConstructor
    public static class TableInfo {

        @JsonProperty("number")
        private final int number;

        @JsonProperty("name")
        private final String name;
    }
}
