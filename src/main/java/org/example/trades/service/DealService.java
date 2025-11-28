package org.example.trades.service;

import org.example.trades.dto.DealDTO;
import org.example.trades.model.DealEntity;

import java.util.List;

public interface DealService {

    DealEntity saveDeal(DealDTO dealDTO);

    List<DealEntity> importDeals(List<DealDTO> deals);

    boolean isDuplicate(String dealId);
}
