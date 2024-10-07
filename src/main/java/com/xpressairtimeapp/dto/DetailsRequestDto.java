package com.xpressairtimeapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailsRequestDto {
    private String phoneNumber;
    private BigDecimal amount;

}
