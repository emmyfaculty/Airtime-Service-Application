package com.xpressairtimeapp;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpressairtimeapp.dto.AirtimeApiResponse;
import com.xpressairtimeapp.dto.AirtimeRequestDto;
import com.xpressairtimeapp.dto.AirtimeResponse;
import com.xpressairtimeapp.dto.DetailsRequestDto;
import com.xpressairtimeapp.serviceimpl.AirtimeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AirtimeServiceImplTest {

    @InjectMocks
    private AirtimeServiceImpl airtimeService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(airtimeService, "privateKey", "jN2ZpsVsF40fvqQ7sRH5RUNs7TIK2kH4_CVASPRV");
        ReflectionTestUtils.setField(airtimeService, "PUBLIC_KEY", "aXShzG4ktSFepMoqY7ZEBuUS8qqrkm9t_CVASPUB");
        ReflectionTestUtils.setField(airtimeService, "apiUrl", "https://billerstest.xpresspayments.com:9603/api/v1/airtime/fulfil");
    }

    @Test
    public void testPurchaseAirtimeSuccess() throws JsonProcessingException {
        // Arrange
        // Arrange
        // Fill the details as per your application's requirement
        DetailsRequestDto detailsRequestDto = DetailsRequestDto.builder()
                .phoneNumber("09132058051")
                .amount(BigDecimal.valueOf(100))
                .build();  // Customize the details as required

        AirtimeRequestDto requestDto = AirtimeRequestDto.builder()
                .requestId("12362")
                .uniqueCode("MTN_19399")
                .details(detailsRequestDto)
                .build();  // Creating a new request DTO with the required fields
        AirtimeApiResponse apiResponse = AirtimeApiResponse.builder()
                .responseCode("00")
                .responseMessage("Success")
                .requestId(Long.valueOf("123"))
                .referenceId("abc")
                .data("TestData")
                .build();

        ResponseEntity<AirtimeApiResponse> mockResponse = ResponseEntity.ok(apiResponse);

        when(objectMapper.writeValueAsString(any())).thenReturn("someStringToHash");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(AirtimeApiResponse.class)))
                .thenReturn(mockResponse);

        // Act
        AirtimeResponse response = airtimeService.purchaseAirtime(requestDto);

        // Assert
        assertNotNull(response);
        assertEquals("00", response.getResponseCode());
        assertEquals("Success", response.getResponseMessage());
        assertEquals("123", response.getAirtimeApiResponse().getRequestId());
        assertEquals("abc", response.getAirtimeApiResponse().getReferenceId());

        // Verify that the restTemplate.exchange() was called once
        verify(restTemplate, times(1)).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(AirtimeApiResponse.class));
    }

    @Test
    public void testPurchaseAirtimeFailure() throws JsonProcessingException {
        // Arrange
        AirtimeRequestDto requestDto = new AirtimeRequestDto();
        AirtimeApiResponse apiResponse = AirtimeApiResponse.builder()
                .responseCode("99")
                .responseMessage("Failed")
                .build();

        ResponseEntity<AirtimeApiResponse> mockResponse = ResponseEntity.ok(apiResponse);

        when(objectMapper.writeValueAsString(any())).thenReturn("someStringToHash");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(AirtimeApiResponse.class)))
                .thenReturn(mockResponse);

        // Act
        AirtimeResponse response = airtimeService.purchaseAirtime(requestDto);

        // Assert
        assertNotNull(response);
        assertEquals("99", response.getResponseCode());
        assertEquals("Failed", response.getResponseMessage());
    }

    @Test
    public void testPurchaseAirtimeException() throws JsonProcessingException {
        // Arrange
        AirtimeRequestDto requestDto = new AirtimeRequestDto();

        when(objectMapper.writeValueAsString(any())).thenReturn("someStringToHash");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(AirtimeApiResponse.class)))
                .thenThrow(new RuntimeException("Error during API call"));

        // Act
        AirtimeResponse response = airtimeService.purchaseAirtime(requestDto);

        // Assert
        assertNotNull(response);
        assertEquals("ERROR", response.getResponseCode());
        assertEquals("Error during API call", response.getResponseMessage());
    }

    @Test
    public void testCalculateHMAC512() {
        // Arrange
        String data = "testData";
        String key = "testKey";

        // Act
        String hmac = AirtimeServiceImpl.calculateHMAC512(data, key);

        // Assert
        assertNotNull(hmac);
    }
}
