package com.muralis.minhasfinancas.api.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBalanceDTO {

    private BigDecimal total;
    private BigDecimal mensal;

}
