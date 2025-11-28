package org.example.trades.controller;

import org.example.trades.dto.DealDTO;
import org.example.trades.model.DealEntity;
import org.example.trades.service.DealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
public class DealController {

    private final DealService dealService;

    @Autowired
    public DealController(DealService dealService) {
        this.dealService = dealService;
    }

    @PostMapping("/save")
    public ResponseEntity<DealEntity> saveDeal(@RequestBody DealDTO dealDTO) {
        DealEntity savedDeal = dealService.saveDeal(dealDTO);
        return ResponseEntity.ok(savedDeal);
    }

    @PostMapping("/import")
    public ResponseEntity<List<DealEntity>> importDeals(@RequestBody List<DealDTO> deals) {
        List<DealEntity> savedDeals = dealService.importDeals(deals);
        return ResponseEntity.ok(savedDeals);
    }

    @GetMapping("/check-duplicate/{dealUniqueId}")
    public ResponseEntity<Boolean> isDuplicate(@PathVariable String dealUniqueId) {
        boolean duplicate = dealService.isDuplicate(dealUniqueId);
        return ResponseEntity.ok(duplicate);
    }
}
