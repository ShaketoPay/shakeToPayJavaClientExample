package com.shakeToPay.examples.clients.testClient.logic;

import com.shakeToPay.examples.clients.testClient.integrations.ServiceException;
import com.shakeToPay.examples.clients.testClient.mapper.JsonUtils;
import com.shakeToPay.examples.clients.testClient.mapper.MapperAbs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class WebsocketDataHandler {

    private final Logic logic;
    private final MapperAbs mapper;

    @Autowired
    WebsocketDataHandler(Logic logic, JsonUtils mapper) {
        this.logic = logic;
        this.mapper = mapper;
    }

    public void update(Object arg) {
        String wsMessage = String.valueOf(arg);
        LOGGER.info("ws message is: {}", wsMessage);

        try {
            PayloadData pd = mapper.buildObject(wsMessage, PayloadData.class);

            if (StringUtils.isBlank(pd.getRID())) {
                LOGGER.error("payload data rID is null or empty!");
                return;
            }
            if (StringUtils.isBlank(pd.getOperation())) {
                LOGGER.error("payload data operation is null or empty!");
                return;
            }
            try {
                switch (pd.getOperation()) {
                    case "checkWork": {
                        logic.performCheckWork(pd.getRID());
                        break;
                    }
                    case "getBill": {
                        logic.performGetBill(pd.getRID(), pd.getTableNumber());
                        break;
                    }
                    case "tablesInfo": {
                        logic.performGetTablesInfo(pd.getRID());
                        break;
                    }
                    default:
                        LOGGER.error("Unknown action: " + pd.getOperation() + "!");
                }
            } catch (ServiceException sEx) {
                LOGGER.error("Can't make logic for " + pd, sEx);
            }
        } catch (IOException ex) {
            LOGGER.error("Can't read payload data!", ex);
        }
    }
}
