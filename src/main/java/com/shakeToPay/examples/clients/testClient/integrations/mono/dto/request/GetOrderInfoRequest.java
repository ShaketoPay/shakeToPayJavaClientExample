package com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
public class GetOrderInfoRequest extends OperationInfo {

    @JsonProperty("tableNumber")
    private final String tableNumber;

    @JsonProperty("orders")
    public List<OrderInfo> orders;

    @JsonProperty("state")
    private final String state = Statuses.SUCCESS_STATUS; // required

    public GetOrderInfoRequest(String rID, String tableNumber, List<OrderInfo> orders) {
        super(rID);
        this.tableNumber = tableNumber;
        this.orders = orders;
    }

    @ToString
    @AllArgsConstructor
    public static class OrderInfo {

        @JsonProperty("billId")
        private final String billId;

        @JsonProperty("orderNumber")
        private final Integer orderNumber;

        @JsonProperty("totalSum")
        private final Double totalSum;

        @JsonProperty("guests")
        private final List<GuestInfo> guests;
    }

    @ToString
    @AllArgsConstructor
    public static class GuestInfo {

        @JsonProperty("name")
        private final String name;

        @JsonProperty("dishes")
        private final List<DishInfo> dishes;
    }

    @ToString
    @AllArgsConstructor
    public static class DishInfo {

        @JsonProperty("name")
        private final String name;

        @JsonProperty("count")
        private final Double count;

        @JsonProperty("sum")
        private final Double sum;

        @JsonProperty("category")
        private final String category;

        @JsonProperty("orderNumber")
        private final String orderNumber;
    }
}