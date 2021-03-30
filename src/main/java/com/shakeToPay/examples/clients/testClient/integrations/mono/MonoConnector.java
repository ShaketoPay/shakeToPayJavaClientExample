package com.shakeToPay.examples.clients.testClient.integrations.mono;

import com.shakeToPay.examples.clients.testClient.integrations.ServiceException;
import com.shakeToPay.examples.clients.testClient.integrations.mono.dto.request.OperationInfo;
import com.shakeToPay.examples.clients.testClient.mapper.JsonUtils;
import com.shakeToPay.examples.clients.testClient.mapper.MapperAbs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import reactor.netty.http.client.HttpClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;

@Slf4j
@Component
public class MonoConnector {

    private static final String COMMON_ACTION_CONTEXT = "/callback";

    private final String secretKey;
    private final WebClient webClient;
    private final MapperAbs mapper;

    @Autowired
    MonoConnector(
            @Value("${MONO_URL}") String monoUrl,
            @Value("${RESTO_ID}") String restoId,
            @Value("${SECRET_KEY}") String secretKey, JsonUtils mapper) {
        this.secretKey = secretKey;

        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofMillis(10000));

        Builder builder = WebClient.builder().baseUrl("https://"
                + monoUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("restoId", restoId);

        webClient = builder.build();

        LOGGER.info("created monoConnector to " + monoUrl + COMMON_ACTION_CONTEXT + " for restaurant " + restoId);
        this.mapper = mapper;
    }

    public void sendCheckWorkInfo(OperationInfo checkWorkResponse) throws ServiceException {
        performSend(checkWorkResponse, COMMON_ACTION_CONTEXT + "/checkWork");
    }

    public void sendBillInfo(OperationInfo getBillInfo) throws ServiceException {
        performSend(getBillInfo, COMMON_ACTION_CONTEXT + "/getBill");
    }

    public void sendTablesInfo(OperationInfo categoriesInfo) throws ServiceException {
        performSend(categoriesInfo, COMMON_ACTION_CONTEXT + "/tablesInfo");
    }

    private void performSend(Object obj, String urlPart) throws ServiceException {
        try {
            String strRepresentation = mapper.buildRepresentation(obj);
            byte[] dataBytes = strRepresentation.getBytes(StandardCharsets.UTF_8);
            String signValue = makeSignature(dataBytes);

            long startTime = System.currentTimeMillis();
            ResponseEntity<String> bodyEntity = webClient.post().uri(urlPart).body(BodyInserters.fromValue(dataBytes))
                    .header("signature", signValue)
                    .retrieve().toEntity(String.class).block();
            long finishTime = System.currentTimeMillis();

            int statusCode = bodyEntity == null ? -1 : bodyEntity.getStatusCode().value();
            String body = bodyEntity == null ? null : bodyEntity.getBody();

            LOGGER.info("send to destination --->>>\n\tRequest: [" + strRepresentation + "]\n\tResponse: code ["
                    + statusCode + "], body: [" + body + "], duration: [" + (finishTime - startTime) + "msec]");
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    private String makeSignature(byte[] bytes) throws InvalidKeyException, NoSuchAlgorithmException {
        Mac hasher = Mac.getInstance("HmacSHA256");
        hasher.init(new SecretKeySpec(secretKey.getBytes(), "HmacSHA256"));
        byte[] hash = hasher.doFinal(bytes);
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(hash);
    }
}
