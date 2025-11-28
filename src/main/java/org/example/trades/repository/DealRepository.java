package org.example.trades.repository;

import org.example.trades.model.DealEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealRepository extends JpaRepository<DealEntity, String> {

    boolean existsByDealUniqueId(String dealId);

}
