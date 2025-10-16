package com.reservAR.backreservar.model;

import com.reservAR.backreservar.exception.InventoryRuleException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "inventory_rule")
public class InventoryRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;
    private int defaultDurationMin;
    private int prepBufferMin;
    private int cleanupBufferMin;
    private int slotGranularityMin;

    @PrePersist
    @PreUpdate
    public void validate(){
        if (prepBufferMin < 0 || defaultDurationMin < 0 || slotGranularityMin < 0 || cleanupBufferMin < 0) {
            throw new InventoryRuleException("Time cannot be negative");
        }
    }
}
