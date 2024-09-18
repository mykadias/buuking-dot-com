package br.com.myka.buuking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationExchangeOnHold implements BuukingEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.CHAR)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "guestId", nullable = false)
    private Guest guest;

    @OneToOne
    @JoinColumn(name = "oldReservationId", nullable = false)
    private Reservation reservation;

    @OneToOne
    @JoinColumn(name = "newReservationId", nullable = false)
    private Reservation newReservation;

    private LocalDateTime expirationDate;
}
