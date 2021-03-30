package com.shakeToPay.examples.clients.testClient.mapper;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class MapperAbs {

    protected abstract ObjectMapper getObjectMapper();

    public String buildRepresentation(Object data2convert) throws JsonProcessingException {
        ObjectMapper objectMapper = getObjectMapper();
        if (data2convert == null) {
            throw new IllegalArgumentException("data2convert is null!");
        }
        return objectMapper.writeValueAsString(data2convert);
    }

    public byte[] buildRepresentationAsBytes(Object data2convert) throws JsonProcessingException {
        ObjectMapper objectMapper = getObjectMapper();
        if (data2convert == null) {
            throw new IllegalArgumentException("data2convert is null!");
        }
        return objectMapper.writeValueAsBytes(data2convert);
    }

    public <T> T buildObject(String representation, Class<T> clazz) throws IOException {
        if (StringUtils.isBlank(representation)) {
            throw new IllegalArgumentException("representation is null!");
        }
        return getObjectMapper().readValue(representation, clazz);
    }

    public <T> T buildObject(byte[] representation, Class<T> clazz) throws IOException {
        if (representation == null) {
            throw new IllegalArgumentException("representation is null!");
        }
        return getObjectMapper().readValue(representation, clazz);
    }
}
