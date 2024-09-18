package br.com.myka.buuking.repository;

import br.com.myka.buuking.entity.ReservationExchangeOnHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationExchangeOnHoldRepository extends JpaRepository<ReservationExchangeOnHold, UUID> {

    Optional<ReservationExchangeOnHold> findReservationExchangeOnHoldByReservation_Id(UUID reservationId);

    List<ReservationExchangeOnHold> findAllByExpirationDateBefore(LocalDateTime cleanUpDate);

    List<ReservationExchangeOnHold> findByGuest_Id(UUID guestId);

}
