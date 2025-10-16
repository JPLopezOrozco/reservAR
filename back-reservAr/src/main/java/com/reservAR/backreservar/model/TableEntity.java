package com.reservAR.backreservar.model;

import com.reservAR.backreservar.exception.TableException;
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
@Table(name = "tables", uniqueConstraints = @UniqueConstraint(columnNames = {"restaurant_id", "code"}))
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;
    private String code;
    private int minCapacity;
    private int maxCapacity;

    @PreUpdate
    @PrePersist
    private void validate(){
        if (minCapacity > maxCapacity || minCapacity < 0) {
            throw new TableException("Min capacity exceeds max capacity");
        }
    }

}
