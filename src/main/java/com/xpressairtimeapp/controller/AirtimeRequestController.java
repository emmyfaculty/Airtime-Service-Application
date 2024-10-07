package com.xpressairtimeapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xpressairtimeapp.dto.AirtimeRequestDto;
import com.xpressairtimeapp.dto.AirtimeResponse;
import com.xpressairtimeapp.service.AirtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/airtime")
@RequiredArgsConstructor
public class AirtimeRequestController {

        private final AirtimeService airtimeService;

    /**
     * Handles the purchase of airtime.
     *
     * @param airtimeRequestDto the request body containing airtime purchase details
     * @return ResponseEntity containing the result of the airtime purchase
     * @throws JsonProcessingException if an error occurs during JSON processing
     */
        @PostMapping("/purchase")
        public ResponseEntity<AirtimeResponse> purchaseAirtime(@RequestBody AirtimeRequestDto airtimeRequestDto) throws JsonProcessingException {
            AirtimeResponse airtimeResponse = airtimeService.purchaseAirtime(airtimeRequestDto);
            return ResponseEntity.ok(airtimeResponse);
        }

}
