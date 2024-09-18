package br.com.myka.buuking;

import br.com.myka.buuking.client.PropertyExternalClient;
import br.com.myka.buuking.converter.AvailablePropertiesConverter;
import br.com.myka.buuking.converter.CustomerReservationExchangeConverter;
import br.com.myka.buuking.converter.ReservationConverter;
import br.com.myka.buuking.entity.*;
import br.com.myka.buuking.exception.ReservationNotFoundException;
import br.com.myka.buuking.exception.RoomNotAvailableException;
import br.com.myka.buuking.exception.ValidationException;
import br.com.myka.buuking.model.request.PaymentRequest;
import br.com.myka.buuking.model.request.ReservationRequest;
import br.com.myka.buuking.model.response.CustomerReservationExchangeResponse;
import br.com.myka.buuking.model.response.ReservationResponse;
import br.com.myka.buuking.repository.ReservationRepository;
import br.com.myka.buuking.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private RoomService roomService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationConverter reservationConverter;

    @Mock
    private GuestService guestService;

    @Mock
    private AvailablePropertiesConverter availablePropertiesConverter;

    @Mock
    private ReservationExchangeOnHoldService reservationExchangeOnHoldService;

    @Mock
    private CustomerReservationExchangeConverter customerReservationExchangeConverter;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PropertyExternalClient propertyExternalClient;

    @Mock
    private CounterService counterService;

    @Captor
    private ArgumentCaptor<Reservation> reservationArgumentCaptor;

    @Test
    void findAll_shouldReturnListOfReservations() {
        // arrange
        Reservation reservation = new Reservation();
        ReservationResponse reservationResponse = new ReservationResponse();
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));
        when(reservationConverter.convert(reservation)).thenReturn(reservationResponse);

        // act
        List<ReservationResponse> result = reservationService.findAll();

        // assert
        assertThat(result).containsExactly(reservationResponse);
        verify(reservationRepository).findAll();
        verify(reservationConverter).convert(reservation);
    }

    @Test
    void findReservationById_shouldReturnReservationResponse() {
        // arrange
        UUID reservationId = UUID.randomUUID();
        Reservation reservation = new Reservation();
        ReservationResponse reservationResponse = new ReservationResponse();
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationConverter.convert(reservation)).thenReturn(reservationResponse);

        // act
        ReservationResponse result = reservationService.findReservationById(reservationId);

        // assert
        assertThat(result).isEqualTo(reservationResponse);
        verify(reservationRepository).findById(reservationId);
        verify(reservationConverter).convert(reservation);
    }

    @Test
    void save_givenValidInput_saveMethodIsCalled() {
        // arrange
        UUID roomId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();

        PaymentRequest paymentRequest = new PaymentRequest();
        ReservationRequest reservationRequest = ReservationRequest.builder()
            .payment(paymentRequest)
            .roomId(roomId)
            .guestId(guestId)
            .build();

        Reservation reservation = Reservation.builder().roomId(roomId).build();
        Room room = Room.builder().id(roomId).build();
        Guest guest = Guest.builder().id(guestId).build();
        Payment payment = Payment.builder().build();

        when(reservationConverter.convert(reservationRequest)).thenReturn(reservation);
        when(roomService.getRoom(reservation.getRoomId())).thenReturn(room);
        when(guestService.getGuest(guestId)).thenReturn(guest);
        when(paymentService.capturePayment(paymentRequest)).thenReturn(payment);
        doNothing().when(propertyExternalClient).reportReservationToProperty(reservation);

        // act
        reservationService.save(reservationRequest);

        // assert
        verify(counterService).reservationPerGuest(guestId);
        assertThat(reservation)
            .extracting("room", "guest", "payment", "acknowledgedByProperty")
            .containsExactly(room, guest, payment, true);
    }

    @Test
    void save_givenValidInput_errorOnPropertyClient() {
        // arrange
        UUID roomId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();

        PaymentRequest paymentRequest = new PaymentRequest();
        ReservationRequest reservationRequest = ReservationRequest.builder()
            .payment(paymentRequest)
            .roomId(roomId)
            .guestId(guestId)
            .build();

        Reservation reservation = Reservation.builder().roomId(roomId).build();
        Room room = Room.builder().id(roomId).build();
        Guest guest = Guest.builder().id(guestId).build();
        Payment payment = Payment.builder().build();

        when(reservationConverter.convert(reservationRequest)).thenReturn(reservation);
        when(roomService.getRoom(reservation.getRoomId())).thenReturn(room);
        when(guestService.getGuest(guestId)).thenReturn(guest);
        when(paymentService.capturePayment(paymentRequest)).thenReturn(payment);
        doThrow(new RuntimeException("Error")).when(propertyExternalClient)
            .reportReservationToProperty(reservation);

        // act
        reservationService.save(reservationRequest);

        // assert
        verify(counterService).reservationPerGuest(guestId);
        assertThat(reservation)
            .extracting("room", "guest", "payment", "acknowledgedByProperty")
            .containsExactly(room, guest, payment, false);
    }

    @Test
    void reserveRoom_noRoomAvailable_exceptionIsThrown() {
        // arrange
        UUID roomId = UUID.randomUUID();

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        ReservationRequest reservationRequest = ReservationRequest.builder()
            .checkOut(today)
            .checkIn(yesterday)
            .roomId(roomId)
            .build();

        when(roomService.findAllAvailableRooms(yesterday, today, null, roomId, null, null))
            .thenReturn(List.of());


        // act && assert
        assertThatThrownBy(() -> reservationService.reserveRoom(reservationRequest))
            .isInstanceOf(ValidationException.class)
            .hasMessage("No available rooms for this period");
    }

    @Test
    void reserveRoom_wrongDateOrder_exceptionIsThrown() {
        // arrange
        UUID roomId = UUID.randomUUID();

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        ReservationRequest reservationRequest = ReservationRequest.builder()
            .checkOut(yesterday)
            .checkIn(today)
            .roomId(roomId)
            .build();

        // act && assert
        assertThatThrownBy(() -> reservationService.reserveRoom(reservationRequest))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Checkin cannot be after checkout");
    }


    @Test
    void reserveRoom_givenValidInput_success() {
        // arrange
        UUID roomId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        PaymentRequest paymentRequest = new PaymentRequest();
        ReservationRequest reservationRequest = ReservationRequest.builder()
            .checkIn(yesterday)
            .checkOut(today)
            .payment(paymentRequest)
            .roomId(roomId)
            .guestId(guestId)
            .build();

        Reservation reservation = Reservation.builder().roomId(roomId).build();
        Room room = Room.builder().id(roomId).build();
        Guest guest = Guest.builder().id(guestId).build();
        Payment payment = Payment.builder().build();

        when(roomService.findAllAvailableRooms(yesterday, today, null, roomId, null, null))
            .thenReturn(List.of(new Room()));
        when(reservationConverter.convert(reservationRequest)).thenReturn(reservation);
        when(roomService.getRoom(reservation.getRoomId())).thenReturn(room);
        when(guestService.getGuest(guestId)).thenReturn(guest);
        when(paymentService.capturePayment(paymentRequest)).thenReturn(payment);
        doNothing().when(propertyExternalClient).reportReservationToProperty(reservation);

        // act
        reservationService.reserveRoom(reservationRequest);

        // assert
        verify(counterService).reservationPerGuest(guestId);
        assertThat(reservation)
            .extracting("room", "guest", "payment", "acknowledgedByProperty")
            .containsExactly(room, guest, payment, true);
    }

    @Test
    void deleteReservation_givenTheId_deleteMethodIsCalled() {
        // act
        UUID id = UUID.randomUUID();

        // arrange
        reservationService.deleteReservation(id);

        // assert
        verify(reservationRepository).deleteById(id);
    }

    @Test
    void update_givenValidInput_shouldUpdateReservation() {
        // arrange
        UUID reservationId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();

        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        ReservationRequest reservationRequest = ReservationRequest.builder()
            .checkIn(checkIn)
            .checkOut(checkOut)
            .roomId(roomId)
            .guestId(guestId)
            .build();

        Reservation existingReservation = Reservation.builder()
            .id(reservationId)
            .roomId(roomId)
            .guestId(guestId)
            .checkIn(LocalDate.now())
            .checkOut(LocalDate.now().plusDays(2))
            .build();

        Reservation updatedReservation = Reservation.builder()
            .id(reservationId)
            .roomId(roomId)
            .guestId(guestId)
            .checkIn(checkIn)
            .checkOut(checkOut)
            .build();

        ReservationResponse reservationResponse = new ReservationResponse();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(existingReservation));
        when(roomService.findAllAvailableRooms(
            checkIn,
            checkOut,
            null,
            roomId,
            reservationId,
            null)).thenReturn(List.of(new Room()));
        when(reservationRepository.save(existingReservation)).thenReturn(updatedReservation);
        when(reservationConverter.convert(updatedReservation)).thenReturn(reservationResponse);

        // act
        ReservationResponse result = reservationService.update(reservationId, reservationRequest);

        // assert
        assertThat(result).isEqualTo(reservationResponse);
        assertThat(existingReservation.getCheckIn()).isEqualTo(checkIn);
        assertThat(existingReservation.getCheckOut()).isEqualTo(checkOut);
        verify(reservationRepository).findById(reservationId);
        verify(reservationRepository).save(existingReservation);
        verify(reservationConverter).convert(updatedReservation);
    }

    @Test
    void update_givenNonExistentReservation_shouldThrowReservationNotFoundException() {
        // arrange
        UUID reservationId = UUID.randomUUID();
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(5);
        ReservationRequest reservationRequest = ReservationRequest.builder()
            .checkOut(checkOut)
            .checkIn(checkIn)
            .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> reservationService.update(reservationId, reservationRequest))
            .isInstanceOf(ReservationNotFoundException.class)
            .hasMessageContaining("Reservation %s not found.".formatted(reservationId));
        verify(reservationRepository).findById(reservationId);
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    void update_givenRoomNotAvailable_shouldThrowRoomNotAvailableException() {
        // arrange
        UUID reservationId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();

        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        ReservationRequest reservationRequest = ReservationRequest.builder()
            .checkIn(checkIn)
            .checkOut(checkOut)
            .roomId(roomId)
            .build();

        Reservation existingReservation = Reservation.builder()
            .id(reservationId)
            .roomId(roomId)
            .checkIn(LocalDate.now())
            .checkOut(LocalDate.now().plusDays(2))
            .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(existingReservation));
        when(roomService.findAllAvailableRooms(
            checkIn,
            checkOut,
            null,
            roomId,
            reservationId,
            null)).thenReturn(List.of());

        // act & assert
        assertThatThrownBy(() -> reservationService.update(reservationId, reservationRequest))
            .isInstanceOf(RoomNotAvailableException.class)
            .hasMessage("Room is not available to the required period");
        verify(reservationRepository).findById(reservationId);
        verify(roomService).findAllAvailableRooms(checkIn, checkOut, null, roomId, reservationId, null);
    }

    @Test
    void update_givenInvalidCheckInAndCheckOut_shouldThrowValidationException() {
        // arrange
        UUID reservationId = UUID.randomUUID();
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(1);

        ReservationRequest reservationRequest = ReservationRequest.builder()
            .checkIn(checkIn)
            .checkOut(checkOut)
            .build();

        // act & assert
        assertThatThrownBy(() -> reservationService.update(reservationId, reservationRequest))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Checkin cannot be after checkout");
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void updateReservation_givenValidInput_reservationNotFound() {
        // arrange
        UUID reservationId = UUID.randomUUID();
        ReservationRequest reservationRequest = ReservationRequest.builder().build();
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> reservationService.updateReservation(reservationId, reservationRequest))
            .isInstanceOf(ReservationNotFoundException.class)
            .hasMessage("Reservation %s not found.".formatted(reservationId));
    }

    @Test
    void updateReservation_givenAlreadyExistingReservationOnHold_shouldReturnTheReservationOnHold() {
        // arrange
        UUID reservationId = UUID.randomUUID();
        ReservationRequest reservationRequest = ReservationRequest.builder().build();
        Reservation reservation = Reservation.builder().build();
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        ReservationExchangeOnHold reservationExchangeOnHold = ReservationExchangeOnHold.builder().build();
        when(reservationExchangeOnHoldService.findByOldReservation(reservation))
            .thenReturn(Optional.of(reservationExchangeOnHold));
        CustomerReservationExchangeResponse customerReservationExchangeResponse = CustomerReservationExchangeResponse
            .builder()
            .build();
        when(customerReservationExchangeConverter.convert(reservationExchangeOnHold))
            .thenReturn(customerReservationExchangeResponse);

        // act & assert
        CustomerReservationExchangeResponse response = reservationService.updateReservation(reservationId, reservationRequest);
        assertThat(response).isNotNull();
    }

    @Test
    void updateReservation_givenValidPossibleUpdate_shouldUpdate() {

        // arrange
        UUID reservationId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();

        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        ReservationRequest reservationRequest = ReservationRequest.builder()
            .checkIn(checkIn)
            .checkOut(checkOut)
            .roomId(roomId)
            .guestId(guestId)
            .build();

        Reservation existingReservation = Reservation.builder()
            .id(reservationId)
            .roomId(roomId)
            .guestId(guestId)
            .checkIn(LocalDate.now())
            .checkOut(LocalDate.now().plusDays(2))
            .build();

        Reservation updatedReservation = Reservation.builder()
            .id(reservationId)
            .roomId(roomId)
            .guestId(guestId)
            .checkIn(checkIn)
            .checkOut(checkOut)
            .build();

        ReservationResponse reservationResponse = new ReservationResponse();

        when(reservationExchangeOnHoldService.findByOldReservation(existingReservation)).thenReturn(Optional.empty());
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(existingReservation));
        when(roomService.findAllAvailableRooms(
            checkIn,
            checkOut,
            null,
            roomId,
            reservationId,
            null)).thenReturn(List.of(new Room()));
        when(reservationRepository.save(existingReservation)).thenReturn(updatedReservation);
        when(reservationConverter.convert(updatedReservation)).thenReturn(reservationResponse);

        // act
        CustomerReservationExchangeResponse response = reservationService.updateReservation(reservationId, reservationRequest);

        // assert
        assertThat(existingReservation.getCheckIn()).isEqualTo(checkIn);
        assertThat(existingReservation.getCheckOut()).isEqualTo(checkOut);
        assertThat(response).isNotNull();
        verify(reservationRepository, times(2)).findById(reservationId);
        verify(reservationRepository).save(existingReservation);
        verify(reservationConverter).convert(updatedReservation);
    }

    @Test
    void updateReservation_givenImpossibleUpdate_noRoomFoundAnywhere() {

        // arrange
        UUID reservationId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();

        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        ReservationRequest reservationRequest = ReservationRequest.builder()
            .checkIn(checkIn)
            .checkOut(checkOut)
            .roomId(roomId)
            .guestId(guestId)
            .build();

        Room roomReservedPreviously = Room.builder()
            .id(roomId)
            .roomNumber("1")
            .property(Property.builder()
                .id(propertyId)
                .build())
            .build();
        Reservation existingReservation = Reservation.builder()
            .id(reservationId)
            .guestId(guestId)
            .room(roomReservedPreviously)
            .checkIn(LocalDate.now())
            .checkOut(LocalDate.now().plusDays(2))
            .build();

        when(reservationExchangeOnHoldService.findByOldReservation(existingReservation)).thenReturn(Optional.empty());
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(existingReservation));
        when(roomService.findAllAvailableRooms(checkIn, checkOut, null, roomId, reservationId, null))
            .thenReturn(List.of());
        when(roomService.findAllAvailableRoomsInAnyProperty(reservationRequest.getCheckIn(), reservationRequest.getCheckOut()))
            .thenReturn(List.of());

        // act & assert
        assertThatThrownBy(() -> reservationService.updateReservation(reservationId, reservationRequest))
            .isInstanceOf(RoomNotAvailableException.class)
            .hasMessage("No room available.");
    }

    @Test
    void updateReservation_givenImpossibleUpdate_roomWasFoundInTheSameProperty() {

        // arrange
        UUID reservationId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();

        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        ReservationRequest reservationRequest = ReservationRequest.builder()
            .checkIn(checkIn)
            .checkOut(checkOut)
            .roomId(roomId)
            .guestId(guestId)
            .build();

        Room roomReservedPreviously = Room.builder()
            .id(roomId)
            .roomNumber("1")
            .property(Property.builder()
                .id(propertyId)
                .build())
            .build();
        Room newRoom = Room.builder()
            .id(roomId)
            .roomNumber("2")
            .property(Property.builder()
                .id(propertyId)
                .build())
            .build();
        Guest guest = Guest.builder()
            .id(guestId)
            .build();
        Reservation existingReservation = Reservation.builder()
            .id(reservationId)
            .guest(guest)
            .room(roomReservedPreviously)
            .checkIn(LocalDate.now())
            .checkOut(LocalDate.now().plusDays(2))
            .build();

        Reservation newReservation = Reservation.builder()
            .room(newRoom)
            .guest(guest)
            .build();

        ReservationExchangeOnHold reservationExchangeOnHold = ReservationExchangeOnHold.builder().build();

        when(reservationExchangeOnHoldService.findByOldReservation(existingReservation)).thenReturn(Optional.empty());
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(existingReservation));
        when(roomService.findAllAvailableRooms(checkIn, checkOut, null, roomId, reservationId, null))
            .thenReturn(List.of());
        when(reservationRepository.save(reservationArgumentCaptor.capture())).thenReturn(newReservation);
        when(roomService.findAllAvailableRoomsInAnyProperty(reservationRequest.getCheckIn(), reservationRequest.getCheckOut()))
            .thenReturn(List.of(newRoom));
        when(reservationExchangeOnHoldService.hold(existingReservation, newReservation))
            .thenReturn(reservationExchangeOnHold);

        // act
        CustomerReservationExchangeResponse response = reservationService.updateReservation(reservationId, reservationRequest);

        // assert
        assertThat(reservationArgumentCaptor.getValue())
            .extracting("room", "checkIn", "checkOut", "guest", "active", "acknowledgedByProperty")
            .containsExactly(newRoom, reservationRequest.getCheckIn(), reservationRequest.getCheckOut(), guest, false, false);

        verify(reservationRepository, times(2)).findById(reservationId);
        verify(reservationRepository).save(any());
        verify(customerReservationExchangeConverter).convert(reservationExchangeOnHold);
    }

    @Test
    void updateReservation_givenImpossibleUpdate_roomWasFoundInAnotherProperty() {

        // arrange
        UUID reservationId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        UUID otherProperty = UUID.randomUUID();

        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        ReservationRequest reservationRequest = ReservationRequest.builder()
            .checkIn(checkIn)
            .checkOut(checkOut)
            .roomId(roomId)
            .guestId(guestId)
            .build();

        Room roomReservedPreviously = Room.builder()
            .id(roomId)
            .roomNumber("1")
            .property(Property.builder()
                .id(propertyId)
                .build())
            .build();
        Room newRoom = Room.builder()
            .id(roomId)
            .roomNumber("2")
            .property(Property.builder()
                .id(otherProperty)
                .build())
            .build();
        Guest guest = Guest.builder()
            .id(guestId)
            .build();
        Reservation existingReservation = Reservation.builder()
            .id(reservationId)
            .guest(guest)
            .room(roomReservedPreviously)
            .checkIn(LocalDate.now())
            .checkOut(LocalDate.now().plusDays(2))
            .build();

        Reservation newReservation = Reservation.builder()
            .room(newRoom)
            .guest(guest)
            .build();

        ReservationExchangeOnHold reservationExchangeOnHold = ReservationExchangeOnHold.builder().build();

        when(reservationExchangeOnHoldService.findByOldReservation(existingReservation)).thenReturn(Optional.empty());
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(existingReservation));
        when(roomService.findAllAvailableRooms(checkIn, checkOut, null, roomId, reservationId, null))
            .thenReturn(List.of());
        when(reservationRepository.save(reservationArgumentCaptor.capture())).thenReturn(newReservation);
        when(roomService.findAllAvailableRoomsInAnyProperty(reservationRequest.getCheckIn(), reservationRequest.getCheckOut()))
            .thenReturn(List.of(newRoom));
        when(reservationExchangeOnHoldService.hold(existingReservation, newReservation))
            .thenReturn(reservationExchangeOnHold);

        // act
        CustomerReservationExchangeResponse response = reservationService.updateReservation(reservationId, reservationRequest);

        // assert
        assertThat(reservationArgumentCaptor.getValue())
            .extracting("room", "checkIn", "checkOut", "guest", "active", "acknowledgedByProperty")
            .containsExactly(newRoom, reservationRequest.getCheckIn(), reservationRequest.getCheckOut(), guest, false, false);

        verify(reservationRepository, times(2)).findById(reservationId);
        verify(reservationRepository).save(any());
        verify(customerReservationExchangeConverter).convert(reservationExchangeOnHold);
    }


}

