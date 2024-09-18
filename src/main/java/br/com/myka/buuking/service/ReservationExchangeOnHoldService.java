package br.com.myka.buuking.service;

import br.com.myka.buuking.converter.CustomerReservationExchangeConverter;
import br.com.myka.buuking.entity.Reservation;
import br.com.myka.buuking.entity.ReservationExchangeOnHold;
import br.com.myka.buuking.exception.ReservationExchangeOnHoldNotFoundException;
import br.com.myka.buuking.model.response.CustomerReservationExchangeResponse;
import br.com.myka.buuking.repository.ReservationExchangeOnHoldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationExchangeOnHoldService {

    private final ReservationExchangeOnHoldRepository reservationExchangeOnHoldRepository;

    private final CustomerReservationExchangeConverter customerReservationExchangeConverter;

    public ReservationExchangeOnHold hold(Reservation oldReservation, Reservation newReservation) {
        return reservationExchangeOnHoldRepository.save(ReservationExchangeOnHold.builder()
            .reservation(oldReservation)
            .newReservation(newReservation)
            .guest(oldReservation.getGuest())
            .expirationDate(LocalDateTime.now().plus(Duration.ofHours(1)))
            .build());
    }

    public Optional<ReservationExchangeOnHold> findByOldReservation(Reservation reservation) {
        return reservationExchangeOnHoldRepository.findReservationExchangeOnHoldByReservation_Id(reservation.getId());
    }

    public ReservationExchangeOnHold findById(UUID id) {
        return reservationExchangeOnHoldRepository.findById(id)
            .orElseThrow(() -> new ReservationExchangeOnHoldNotFoundException(id));
    }

    public List<ReservationExchangeOnHold> findAllExpiredReservations() {
        return reservationExchangeOnHoldRepository.findAllByExpirationDateBefore(LocalDateTime.now());
    }

    public void delete(ReservationExchangeOnHold item) {
        reservationExchangeOnHoldRepository.delete(item);
    }

    public List<CustomerReservationExchangeResponse> findAllByGuestId(UUID guestId) {
        return reservationExchangeOnHoldRepository.findByGuest_Id(guestId)
            .stream()
            .map(customerReservationExchangeConverter::convert)
            .toList();
    }
}
