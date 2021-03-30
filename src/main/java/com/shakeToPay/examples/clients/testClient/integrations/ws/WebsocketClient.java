package com.shakeToPay.examples.clients.testClient.integrations.ws;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import javax.websocket.ClientEndpointConfig.Builder;
import javax.websocket.ClientEndpointConfig.Configurator;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
class WebsocketClient {

    private final WebsocketEndpoint websocketEndpoint;
    private final ClientEndpointConfig clientConfig;
    private final URI uri;
    private boolean connected;

    WebsocketClient(String uri, String user, String password, MessageHandler.Whole<String> messageHandler) throws URISyntaxException {
        Builder configBuilder = Builder.create();

        Base64.Encoder encoder = Base64.getEncoder();
        String authHeaderValue = "Basic " + encoder.encodeToString((user + ":" + password).getBytes());

        configBuilder.configurator(new Configurator() {
            @Override
            public void beforeRequest(Map<String, List<String>> headers) {
                headers.put("Authorization", Arrays.asList(authHeaderValue));
            }
        });

        clientConfig = configBuilder.build();

        this.uri = new URI(uri);

        LOGGER.info("Connect ws params uri: {}, Authorization header: {}", uri, authHeaderValue);

        this.websocketEndpoint = new WebsocketEndpoint(messageHandler, new EventListenerI() {

            @Override
            public void notify(Object o) {
                connected = false;
            }
        });
    }

    void connect() throws IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(websocketEndpoint, clientConfig, uri);
            connected = true;
        } catch (DeploymentException e) {
            connected = false;
            throw new IOException("Error while connecting to websocket server: " + uri, e);
        }
    }

    boolean sendMessage(String message) throws IOException {
        return sendMessage(message, false);
    }

    boolean sendMessage(String message, boolean isPing) throws IOException {
        if (websocketEndpoint.session == null) {
            return false;
        }
        if (isPing) {
            websocketEndpoint.session.getBasicRemote().sendPing(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
        } else {
            websocketEndpoint.session.getBasicRemote().sendText(message);
        }
        return true;
    }

    void close() throws IOException {
        connected = false;
        if (websocketEndpoint.session != null) {
            websocketEndpoint.session.close();
        }
    }

    boolean isConnected() {
        return connected;
    }
}

@Slf4j
class WebsocketEndpoint extends Endpoint {

    Session session = null;
    private final MessageHandler.Whole<String> messageHandler;
    private final EventListenerI closeEventListener;

    WebsocketEndpoint(MessageHandler.Whole<String> messageHandler, EventListenerI closeEventListener) {
        this.messageHandler = messageHandler;
        this.closeEventListener = closeEventListener;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        if (messageHandler != null) {
            session.addMessageHandler(new MessageHandler.Whole<String>() {

                @Override
                public void onMessage(String message) {
                    messageHandler.onMessage(message);
                }
            });
        }
    }

    @Override
    public void onError(Session session, Throwable throwable) {
        try {
            LOGGER.error("websocket error: ", throwable);
            super.onError(session, throwable);
        } catch (Exception ex) {
            LOGGER.error("websocket onError: ", ex);
        }
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        LOGGER.error("close websocket connection, resason: {}", closeReason);
        super.onClose(session, closeReason);
        closeEventListener.notify(null);
    }
}

interface EventListenerI {
    void notify(Object o);
}
