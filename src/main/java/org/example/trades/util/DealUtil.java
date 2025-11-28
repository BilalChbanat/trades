package org.example.trades.util;

import org.example.trades.dto.DealDTO;
import org.example.trades.model.DealEntity;

import java.time.Instant;
import java.time.ZoneOffset;

public class DealUtil {

    public static DealEntity toEntity(DealDTO dto) {
        DealEntity entity = new DealEntity();
        entity.setDealUniqueId(dto.getDealUniqueId());
        entity.setFromCurrency(dto.getFromCurrencyIso() != null ? dto.getFromCurrencyIso().toUpperCase() : null);
        entity.setToCurrency(dto.getToCurrencyIso() != null ? dto.getToCurrencyIso().toUpperCase() : null);
        entity.setDealTimestamp(dto.getDealTimestamp() != null ? 
            dto.getDealTimestamp().toInstant(ZoneOffset.UTC) : null);
        entity.setAmount(dto.getDealAmount());
        entity.setCreatedAt(Instant.now());
        return entity;
    }
}
