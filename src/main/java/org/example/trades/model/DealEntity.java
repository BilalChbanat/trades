package org.example.trades.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "deal")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DealEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 100)
    private String dealUniqueId;

    @Column(nullable = false, length = 3)
    private String fromCurrency;

    @Column(nullable = false, length = 3)
    private String toCurrency;

    @Column(nullable = false)
    private Instant dealTimestamp;

    @Column(nullable = false, precision = 20, scale = 6)
    private BigDecimal amount;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();


}
