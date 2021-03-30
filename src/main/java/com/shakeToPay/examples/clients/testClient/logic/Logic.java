package com.shakeToPay.examples.clients.testClient.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shakeToPay.examples.clients.testClient.integrations.ServiceException;
import com.shakeToPay.examples.clients.testClient.integrations.mono.MonoConnector;
import com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request.CheckWorkRequest;
import com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request.ErrorResponse;
import com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request.GetOrderInfoRequest;
import com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request.GetOrderInfoRequest.DishInfo;
import com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request.GetOrderInfoRequest.GuestInfo;
import com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request.GetOrderInfoRequest.OrderInfo;
import com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request.OperationInfo;
import com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request.TablesRequest;
import com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request.TablesRequest.TableInfo;

@Component
class Logic {

    private final MonoConnector monoConnector;

    @Autowired
    Logic(MonoConnector monoConnector) {
        this.monoConnector = monoConnector;
    }

    void performCheckWork(String rID) throws ServiceException {
        monoConnector.sendCheckWorkInfo(new CheckWorkRequest(rID));
    }

    void performGetTablesInfo(String rID) throws ServiceException {
        List<TableInfo> tables = new ArrayList<>();
        tables.add(new TableInfo(12, "Столик у окна"));
        tables.add(new TableInfo(7, "Столик на двоих"));
        monoConnector.sendTablesInfo(new TablesRequest(rID, tables));
    }

    void performGetBill(String rID, String tableNumber) throws ServiceException {
        if (!"1".equals(tableNumber)) {
            this.monoConnector.sendBillInfo(
                    new ErrorResponse(rID, "TABLE_NOT_FOUND", null, "тут находится деталировка что случилось"));
        } else {
            List<DishInfo> dishes = new ArrayList<>();
            dishes.add(new DishInfo("Картошка", 2., 20., "Еда", "12"));
            dishes.add(new DishInfo("Пиво пилснер", 0.5, 30.50, "Выпивка", "12"));

            GuestInfo guest = new GuestInfo("Валерий Павлович", dishes);
            OrderInfo orderInfo = new OrderInfo("billId", 1, Double.valueOf(70.50), Collections.singletonList(guest));

            OperationInfo request = new GetOrderInfoRequest(rID, tableNumber, Collections.singletonList(orderInfo));
            this.monoConnector.sendBillInfo(request);
        }
    }
}
