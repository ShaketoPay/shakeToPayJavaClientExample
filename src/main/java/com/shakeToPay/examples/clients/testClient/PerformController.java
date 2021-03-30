package com.shakeToPay.examples.clients.testClient;

import com.shakeToPay.examples.clients.testClient.logic.WebsocketDataHandler;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
class PerformController {

    private final WebsocketDataHandler dataHandler;

    /**
     * Данный метод можно использовать для эмуляции получения сообщения по
     * вебсокету.
     */
    @PostMapping(path = "/performRequest", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    String performRequest(@RequestBody String message) {
        dataHandler.update(message);
        return "{\"status\":\"ok\"}";
    }

}
