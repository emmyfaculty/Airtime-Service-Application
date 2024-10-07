package com.xpressairtimeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xpressairtimeapp.dto.AirtimeRequestDto;
import com.xpressairtimeapp.dto.AirtimeResponse;
import com.xpressairtimeapp.entity.User;

import java.math.BigDecimal;

public interface AirtimeService {
    AirtimeResponse purchaseAirtime(AirtimeRequestDto airtimeRequestDto) throws JsonProcessingException;
}
