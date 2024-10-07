package com.xpressairtimeapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AirtimeResponse {

	private String responseCode;
	private String responseMessage;
	private WalletInfo walletInfo;
	private AirtimeApiResponse airtimeApiResponse;

}
