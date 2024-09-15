package br.com.myka.buuking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room implements BuukingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.CHAR)
    private UUID id;

    @Column(nullable = false)
    private String roomNumber;

    @Column(nullable = false)
    private BigDecimal pricePerNight;

    @ManyToOne
    @JoinColumn(name = "propertyId", nullable = false)
    private Property property;

    @Transient
    private UUID propertyId;

    public UUID getPropertyId() {
        if (propertyId == null) {
            propertyId = property.getId();
        }
        return propertyId;
    }
}
