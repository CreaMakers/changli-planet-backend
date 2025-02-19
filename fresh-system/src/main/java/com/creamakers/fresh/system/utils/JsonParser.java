package com.creamakers.fresh.system.utils;


import com.creamakers.fresh.system.domain.vo.request.FreshNewsRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser {
    public static FreshNewsRequest StoJ(String s) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        FreshNewsRequest freshNewsRequest = objectMapper.readValue(s, FreshNewsRequest.class);
        return freshNewsRequest;
    }
}
