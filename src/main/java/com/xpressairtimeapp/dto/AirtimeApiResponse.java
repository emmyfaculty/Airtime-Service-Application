package com.xpressairtimeapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AirtimeApiResponse {

	private Long requestId;
	private String referenceId;
	private String responseCode;
	private String responseMessage;
	private Object data;

}
