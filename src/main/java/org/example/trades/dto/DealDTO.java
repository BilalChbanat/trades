package org.example.trades.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DealDTO {

    private String dealUniqueId;
    private String fromCurrencyIso;
    private String toCurrencyIso;
    private LocalDateTime dealTimestamp;
    private BigDecimal dealAmount;


}
