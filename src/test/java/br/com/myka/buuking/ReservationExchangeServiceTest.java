package br.com.myka.buuking;

import br.com.myka.buuking.converter.CustomerReservationExchangeConverter;
import br.com.myka.buuking.entity.Guest;
import br.com.myka.buuking.entity.Reservation;
import br.com.myka.buuking.entity.ReservationExchangeOnHold;
import br.com.myka.buuking.exception.ReservationExchangeOnHoldNotFoundException;
import br.com.myka.buuking.model.response.CustomerReservationExchangeResponse;
import br.com.myka.buuking.repository.ReservationExchangeOnHoldRepository;
import br.com.myka.buuking.service.ReservationExchangeOnHoldService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationExchangeServiceTest {

    @InjectMocks
    private ReservationExchangeOnHoldService reservationExchangeOnHoldService;

    @Mock
    private ReservationExchangeOnHoldRepository reservationExchangeOnHoldRepository;

    @Mock
    private CustomerReservationExchangeConverter customerReservationExchangeConverter;

    @Test
    void findByOldReservation_shouldReturnReservationOnHold() {
        // arrange
        UUID reservationId = UUID.randomUUID();
        Reservation reservation = mock(Reservation.class);
        when(reservation.getId()).thenReturn(reservationId);

        Optional<ReservationExchangeOnHold> expected = Optional.of(mock(ReservationExchangeOnHold.class));
        when(reservationExchangeOnHoldRepository.findReservationExchangeOnHoldByReservation_Id(reservationId))
            .thenReturn(expected);

        // act
        Optional<ReservationExchangeOnHold> result = reservationExchangeOnHoldService.findByOldReservation(reservation);

        // assert
        assertThat(result).isEqualTo(expected);
        verify(reservationExchangeOnHoldRepository).findReservationExchangeOnHoldByReservation_Id(reservationId);
    }

    @Test
    void findById_shouldReturnReservationOnHoldById() {
        // arrange
        UUID id = UUID.randomUUID();
        ReservationExchangeOnHold expected = mock(ReservationExchangeOnHold.class);
        when(reservationExchangeOnHoldRepository.findById(id)).thenReturn(Optional.of(expected));

        // act
        ReservationExchangeOnHold result = reservationExchangeOnHoldService.findById(id);

        // assert
        assertThat(result).isEqualTo(expected);
        verify(reservationExchangeOnHoldRepository).findById(id);
    }

    @Test
    void findById_whenReservationNotFound_shouldThrowException() {
        // arrange
        UUID id = UUID.randomUUID();
        when(reservationExchangeOnHoldRepository.findById(id)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ReservationExchangeOnHoldNotFoundException.class, () -> reservationExchangeOnHoldService.findById(id));
        verify(reservationExchangeOnHoldRepository).findById(id);
    }

    @Test
    void findAllExpiredReservations_shouldReturnExpiredReservations() {
        // arrange
        List<ReservationExchangeOnHold> expected = List.of(mock(ReservationExchangeOnHold.class));
        when(reservationExchangeOnHoldRepository.findAllByExpirationDateBefore(Mockito.any(LocalDateTime.class)))
            .thenReturn(expected);

        // act
        List<ReservationExchangeOnHold> result = reservationExchangeOnHoldService.findAllExpiredReservations();

        // assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void delete_shouldDeleteReservationOnHold() {
        // arrange
        ReservationExchangeOnHold reservationOnHold = mock(ReservationExchangeOnHold.class);

        // act
        reservationExchangeOnHoldService.delete(reservationOnHold);

        // assert
        verify(reservationExchangeOnHoldRepository).delete(reservationOnHold);
    }

    @Test
    void findAllByGuestId_shouldReturnReservationsByGuestId() {
        // arrange
        UUID guestId = UUID.randomUUID();
        List<ReservationExchangeOnHold> reservationsOnHold = List.of(mock(ReservationExchangeOnHold.class));
        List<CustomerReservationExchangeResponse> expectedResponse = List.of(mock(CustomerReservationExchangeResponse.class));

        when(reservationExchangeOnHoldRepository.findByGuest_Id(guestId)).thenReturn(reservationsOnHold);
        when(customerReservationExchangeConverter.convert(any(ReservationExchangeOnHold.class)))
            .thenReturn(expectedResponse.getFirst());

        // act
        List<CustomerReservationExchangeResponse> result = reservationExchangeOnHoldService.findAllByGuestId(guestId);

        // assert
        assertThat(result).isEqualTo(expectedResponse);
        verify(reservationExchangeOnHoldRepository).findByGuest_Id(guestId);
        verify(customerReservationExchangeConverter, times(reservationsOnHold.size())).convert(any(ReservationExchangeOnHold.class));
    }

    @Test
    void hold_shouldSaveReservationExchangeOnHold() {
        // arrange
        Reservation oldReservation = mock(Reservation.class);
        Reservation newReservation = mock(Reservation.class);
        Guest guest = mock(Guest.class);

        when(oldReservation.getGuest()).thenReturn(guest);

        ReservationExchangeOnHold expectedReservationOnHold = ReservationExchangeOnHold.builder()
            .reservation(oldReservation)
            .newReservation(newReservation)
            .guest(guest)
            .expirationDate(LocalDateTime.now().plus(Duration.ofHours(1)))
            .build();

        when(reservationExchangeOnHoldRepository.save(any(ReservationExchangeOnHold.class)))
            .thenReturn(expectedReservationOnHold);

        // act
        ReservationExchangeOnHold result = reservationExchangeOnHoldService.hold(oldReservation, newReservation);

        // assert
        assertThat(result).isEqualTo(expectedReservationOnHold);
        verify(reservationExchangeOnHoldRepository).save(any(ReservationExchangeOnHold.class));
    }
}
