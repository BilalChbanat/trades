package org.example.trades.service.impl;

import org.springframework.transaction.annotation.Transactional;
import org.example.trades.dto.DealDTO;
import org.example.trades.exception.DuplicateDealException;
import org.example.trades.exception.DealValidationException;
import org.example.trades.model.DealEntity;
import org.example.trades.repository.DealRepository;
import org.example.trades.service.DealService;
import org.example.trades.util.DealUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class DealServiceImpl implements DealService {

    private static final Logger logger = LoggerFactory.getLogger(DealServiceImpl.class);

    private final DealRepository dealRepository;
    private final Validator validator;

    @Autowired
    public DealServiceImpl(DealRepository dealRepository, Validator validator) {
        this.dealRepository = dealRepository;
        this.validator = validator;
    }

    @Override
    @Transactional
    public DealEntity saveDeal(DealDTO dealDTO) {
        logger.debug("Saving deal with unique ID: {}", dealDTO.getDealUniqueId());

        // Validate DTO
        validateDealDTO(dealDTO);

        // Check for duplicates
        if (isDuplicate(dealDTO.getDealUniqueId())) {
            String errorMessage = String.format("Deal with unique ID '%s' already exists", dealDTO.getDealUniqueId());
            logger.warn(errorMessage);
            throw new DuplicateDealException(errorMessage);
        }

        // Convert DTO to Entity
        DealEntity entity = DealUtil.toEntity(dealDTO);

        // Save entity (no rollback - each save is committed immediately)
        try {
            DealEntity saved = dealRepository.save(entity);
            logger.info("Successfully saved deal with unique ID: {}", saved.getDealUniqueId());
            return saved;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Handle duplicate key constraint violation
            if (e.getMessage() != null && e.getMessage().contains("deal_unique_id")) {
                String errorMessage = String.format("Deal with unique ID '%s' already exists", dealDTO.getDealUniqueId());
                logger.warn(errorMessage);
                throw new DuplicateDealException(errorMessage);
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public List<DealEntity> importDeals(List<DealDTO> deals) {
        logger.info("Starting import of {} deals", deals.size());

        List<DealEntity> savedDeals = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        int processedCount = 0;
        int savedCount = 0;
        int skippedCount = 0;

        // Process each deal individually - no rollback allowed
        for (DealDTO dealDTO : deals) {
            processedCount++;
            try {
                // Validate
                validateDealDTO(dealDTO);

                // Check for duplicates
                if (isDuplicate(dealDTO.getDealUniqueId())) {
                    String errorMessage = String.format("Deal with unique ID '%s' already exists (row %d)", 
                        dealDTO.getDealUniqueId(), processedCount);
                    logger.warn(errorMessage);
                    errors.add(errorMessage);
                    skippedCount++;
                    continue; // Skip this deal, continue with next
                }

                // Convert and save
                DealEntity entity = DealUtil.toEntity(dealDTO);
                DealEntity saved = dealRepository.save(entity);
                savedDeals.add(saved);
                savedCount++;
                logger.debug("Saved deal {} of {}: {}", savedCount, deals.size(), saved.getDealUniqueId());

            } catch (DuplicateDealException e) {
                errors.add(e.getMessage());
                skippedCount++;
                logger.warn("Skipped duplicate deal at row {}: {}", processedCount, e.getMessage());
            } catch (DealValidationException e) {
                errors.add(String.format("Row %d: %s", processedCount, e.getMessage()));
                skippedCount++;
                logger.warn("Skipped invalid deal at row {}: {}", processedCount, e.getMessage());
            } catch (Exception e) {
                String errorMessage = String.format("Row %d: Unexpected error - %s", processedCount, e.getMessage());
                errors.add(errorMessage);
                skippedCount++;
                logger.error("Error processing deal at row {}: {}", processedCount, e.getMessage(), e);
            }
        }

        logger.info("Import completed. Total: {}, Saved: {}, Skipped: {}", 
            processedCount, savedCount, skippedCount);

        if (!errors.isEmpty() && savedDeals.isEmpty()) {
            throw new DealValidationException("All deals failed validation: " + String.join("; ", errors));
        }

        return savedDeals;
    }

    @Override
    public boolean isDuplicate(String dealUniqueId) {
        if (dealUniqueId == null || dealUniqueId.trim().isEmpty()) {
            return false;
        }
        return dealRepository.existsByDealUniqueId(dealUniqueId.trim());
    }

    private void validateDealDTO(DealDTO dealDTO) {
        if (dealDTO == null) {
            throw new DealValidationException("Deal DTO cannot be null");
        }

        Set<ConstraintViolation<DealDTO>> violations = validator.validate(dealDTO);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            for (ConstraintViolation<DealDTO> violation : violations) {
                errorMessage.append(violation.getPropertyPath())
                    .append(" - ")
                    .append(violation.getMessage())
                    .append("; ");
            }
            throw new DealValidationException(errorMessage.toString().trim());
        }
    }
}

