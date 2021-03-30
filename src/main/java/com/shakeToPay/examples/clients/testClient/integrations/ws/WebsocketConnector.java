package com.shakeToPay.examples.clients.testClient.integrations.ws;

import com.shakeToPay.examples.clients.testClient.logic.WebsocketDataHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URISyntaxException;

@Slf4j
@Component
public class WebsocketConnector implements DisposableBean {

    private static final String URL_PATH_DELIMETER = "/";

    private final WebsocketClient wsClient;
    private final Monitor monitor;

    @Autowired
    WebsocketConnector(
            @Value("${MONO_URL}") String url,
            @Value("${RESTO_ID}") String user,
            @Value("${SECRET_KEY}") String pass, @Value("${reconnect.timeout:15000}") int reConnectTimeout,
            WebsocketDataHandler dataHandler) throws URISyntaxException {

        StringBuilder fullUrlContainer = new StringBuilder("wss://" + url);
        if (!StringUtils.endsWithIgnoreCase(url, URL_PATH_DELIMETER)) {
            fullUrlContainer.append(URL_PATH_DELIMETER);
        }
        fullUrlContainer.append("restaurantEntryPoint");

        wsClient = new WebsocketClient(fullUrlContainer.toString(), user, pass, message -> dataHandler.update( message));

        monitor = new Monitor(reConnectTimeout, user);
        monitor.setRunning(true);

        Thread monitorThread = new Thread(monitor, "WebsocketConnect-monitor-" + user);
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    @Override
    public void destroy() throws Exception {
        if (wsClient != null) {
            wsClient.close();
        }
        if (monitor != null) {
            monitor.setRunning(false);
        }
    }

    private class Monitor implements Runnable {

        private final Object lock = new Object();
        private boolean running;
        private final int reConnectTimeout;
        private final String user;

        public Monitor(int reConnectTimeout, String user) {
            this.reConnectTimeout = reConnectTimeout;
            this.user = user;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    if (wsClient.isConnected()) {
                        LOGGER.info("connected :)");
                        wsClient.sendMessage("Resto " + user + " ping", false);
                    } else {
                        synchronized (lock) {
                            if (!wsClient.isConnected()) {
                                LOGGER.info("try connect ====>>>>> :(");
                                wsClient.connect();
                                LOGGER.info("connected :)");
                            }
                        }
                    }

                } catch (Exception ex) {
                    LOGGER.error("catch problems in socketMonitor: ", ex);
                }
                try {
                    Thread.sleep(reConnectTimeout);
                } catch (InterruptedException ex) {
                    LOGGER.warn("Interrupted!", ex);
                    Thread.currentThread().interrupt();
                }
            }
        }

        void setRunning(boolean running) {
            this.running = running;
        }
    }
}
