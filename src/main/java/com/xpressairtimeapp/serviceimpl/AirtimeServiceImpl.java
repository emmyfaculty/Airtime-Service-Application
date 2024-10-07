package com.xpressairtimeapp.serviceimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpressairtimeapp.dto.AirtimeApiResponse;
import com.xpressairtimeapp.dto.AirtimeRequestDto;
import com.xpressairtimeapp.dto.AirtimeResponse;
import com.xpressairtimeapp.service.AirtimeService;
import com.xpressairtimeapp.utils.WalletUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AirtimeServiceImpl implements AirtimeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AirtimeServiceImpl.class);


    @Value("${airtime.api.url}")
    private String apiUrl; // Base URL for the airtime API

    @Value("${airtime.public.key}")
    private String PUBLIC_KEY;

    @Value("${airtime.private.key}")
    private String privateKey; // Secret key for HMAC signing


    private final ObjectMapper objectMapper;

    /**
     * Purchases airtime by sending a request to the external API.
     *
     * @param airtimeRequestDto the request details for purchasing airtime
     * @return AirtimeResponse containing the result of the purchase
     * @throws JsonProcessingException if an error occurs during the purchase process
     */
    public AirtimeResponse purchaseAirtime(AirtimeRequestDto airtimeRequestDto) throws JsonProcessingException {

        // Prepare data for HMAC signing
        var toHash =  objectMapper.writeValueAsString(airtimeRequestDto); // Convert request to string (implement toString() appropriately)

        var GENERATED_HMAC = calculateHMAC512(toHash, privateKey );  // Calculate HMAC signature

        // Create HTTP headers including HMAC signature
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + PUBLIC_KEY);
        headers.set("PaymentHash", GENERATED_HMAC);
        headers.set("Channel", "API");
        headers.set("Content-Type", "application/json");

        // Create the request entity
        var requestEntity = new HttpEntity<>(airtimeRequestDto, headers);

        try {
            // Send POST request to purchase airtime
            var responseEntity = new RestTemplate().exchange(
                    apiUrl, HttpMethod.POST, requestEntity, AirtimeApiResponse.class);

            // Return the airtime purchase response
            var airtimeApiResponse = responseEntity.getBody();

            if (Objects.nonNull(airtimeApiResponse) && "00".equals(airtimeApiResponse.getResponseCode())) {
                return AirtimeResponse.builder()
                        .responseCode(airtimeApiResponse.getResponseCode())
                        .responseMessage(airtimeApiResponse.getResponseMessage())
                        .airtimeApiResponse(AirtimeApiResponse.builder()
                                .requestId(airtimeApiResponse.getRequestId())
                                .referenceId(airtimeApiResponse.getReferenceId())
                                .data(airtimeApiResponse.getData())
                                .build())
                        .build();
            } else {
                return AirtimeResponse.builder()
                        .responseCode(WalletUtils.FAILED_TRANSACTION_CODE)
                        .responseMessage(WalletUtils.FAILED_TRANSACTION_MESSAGE)
                        .airtimeApiResponse(null)
                        .build();
            }

        } catch (RestClientException e) {

            LOGGER.error("Error occurred during airtime purchase: ", e);
            return new AirtimeResponse("ERROR", e.getMessage(), null, null);
        }
    }

    /**
     * Calculates the HMAC SHA-512 signature for the given data using the provided key.
     *
     * @param data the data to sign
     * @param key the key to use for signing
     * @return the calculated HMAC signature as a hex string
     */
    public static String calculateHMAC512(String data, String key) {

        String HMAC_SHA512 = "HmacSHA512";

        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA512);

        Mac mac = null;

        try {

            mac = Mac.getInstance(HMAC_SHA512);

            mac.init(secretKeySpec);

            return Hex.encodeHexString(mac.doFinal(data.getBytes())); // Convert byte array to hex string

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e.getMessage()); // Handle HMAC calculation failure

        }

    }
}

