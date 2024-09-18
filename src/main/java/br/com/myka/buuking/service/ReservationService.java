package br.com.myka.buuking.service;

import br.com.myka.buuking.client.PropertyExternalClient;
import br.com.myka.buuking.converter.AvailablePropertiesConverter;
import br.com.myka.buuking.converter.CustomerReservationExchangeConverter;
import br.com.myka.buuking.converter.ReservationConverter;
import br.com.myka.buuking.entity.Guest;
import br.com.myka.buuking.entity.Reservation;
import br.com.myka.buuking.entity.Room;
import br.com.myka.buuking.exception.ReservationNotFoundException;
import br.com.myka.buuking.exception.RoomNotAvailableException;
import br.com.myka.buuking.exception.ValidationException;
import br.com.myka.buuking.model.request.PaymentRequest;
import br.com.myka.buuking.model.request.ReservationRequest;
import br.com.myka.buuking.model.response.AvailablePropertiesResponse;
import br.com.myka.buuking.model.response.CustomerReservationExchangeResponse;
import br.com.myka.buuking.model.response.ReservationResponse;
import br.com.myka.buuking.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final String NOT_POSSIBLE_TO_UPDATE_RESERVATION_TRYING_ANOTHER_ROOM = "Not possible to update reservation %s for the same room number. " +
            "Trying to find another room in the same property";
    private final ReservationRepository reservationRepository;

    private final ReservationConverter reservationConverter;

    private final RoomService roomService;

    private final AvailablePropertiesConverter availablePropertiesConverter;

    private final GuestService guestService;

    private final ReservationExchangeOnHoldService reservationExchangeOnHoldService;

    private final CustomerReservationExchangeConverter customerReservationExchangeConverter;

    private final PaymentService paymentService;

    private final PropertyExternalClient propertyExternalClient;

    private final CounterService counterService;

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream().map(reservationConverter::convert).toList();
    }

    public ReservationResponse findReservationById(UUID id) {
        return reservationRepository.findById(id).map(reservationConverter::convert)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    public ReservationResponse save(ReservationRequest reservationRequest) {
        var reservation = reservationConverter.convert(reservationRequest);
        reservation.setRoom(roomService.getRoom(reservation.getRoomId()));
        reservation.setGuest(guestService.getGuest(reservationRequest.getGuestId()));
        reservation.setPayment(paymentService.capturePayment(reservationRequest.getPayment()));
        reservation.setActive(true);
        reportReservationToProperty(reservation);
        var reservationResponse = reservationConverter.convert(reservationRepository.save(reservation));

        counterService.reservationPerGuest(reservation.getGuestId());
        return reservationResponse;
    }

    public void deleteReservation(UUID id) {
        reservationRepository.deleteById(id);
    }


    public ReservationResponse update(UUID id, ReservationRequest reservationRequest) {
        LocalDate checkIn = reservationRequest.getCheckIn();
        LocalDate checkOut = reservationRequest.getCheckOut();

        validateCheckingAndCheckout(checkIn, checkOut);

        var reservation = reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException(id));

        if (roomService.findAllAvailableRooms(
                checkIn,
                checkOut,
                null,
                reservationRequest.getRoomId(),
                reservation.getId(),
                null).isEmpty()) {
            throw new RoomNotAvailableException("Room is not available to the required period");
        }
        reservation.setCheckOut(reservationRequest.getCheckOut());
        reservation.setCheckIn(reservationRequest.getCheckIn());
        return reservationConverter.convert(reservationRepository.save(reservation));
    }

    public CustomerReservationExchangeResponse updateReservation(UUID id, ReservationRequest reservationRequest) {
        var reservation = reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException(id));
        var reservationExchangeOnHold = reservationExchangeOnHoldService.findByOldReservation(reservation);

        if (reservationExchangeOnHold.isPresent()) {
            return customerReservationExchangeConverter.convert(reservationExchangeOnHold.get());
        }

        try {
            ReservationResponse response = update(id, reservationRequest);
            return CustomerReservationExchangeResponse.builder()
                    .newRoom(roomService.getRoomById(response.getRoomId()))
                    .oldRoom(roomService.convertToRoomResponse(reservation.getRoom()))
                    .message("Reservation updated correctly")
                    .build();
        } catch (RoomNotAvailableException ex) {
            log.info(NOT_POSSIBLE_TO_UPDATE_RESERVATION_TRYING_ANOTHER_ROOM.formatted(reservationRequest));
            List<Room> availableRooms = roomService.findAllAvailableRoomsInAnyProperty(
                    reservationRequest.getCheckIn(),
                    reservationRequest.getCheckOut());

            if (availableRooms.isEmpty()) {
                throw new RoomNotAvailableException("No room available.");
            }

            List<Room> roomsInTheSameProperty = availableRooms.stream()
                    .filter(room -> room.getPropertyId().equals(reservation.getRoom().getPropertyId()))
                    .toList();

            Room availableRoom;
            if (!roomsInTheSameProperty.isEmpty()) {
                availableRoom = roomsInTheSameProperty.getFirst();
            } else {
                availableRoom = availableRooms.getFirst();
            }

            Reservation newInactiveReservation = makeReservation(
                    reservationRequest.getCheckIn(),
                    reservationRequest.getCheckOut(),
                    reservation.getGuest(),
                    availableRoom, false);

            var holdReservationExchange = reservationExchangeOnHoldService.hold(reservation, newInactiveReservation);

            return customerReservationExchangeConverter.convert(holdReservationExchange);
        }

    }

    public Reservation makeReservation(LocalDate checkin, LocalDate checkout, Guest guest, Room room, boolean active) {
        return reservationRepository.save(
                Reservation.builder()
                        .checkIn(checkin)
                        .checkOut(checkout)
                        .room(room)
                        .guest(guest)
                        .active(active)
                        .build()
        );
    }

    public AvailablePropertiesResponse findAvailableRoom(LocalDate checkin, LocalDate checkout, Optional<String> hotelName) {
        validateCheckingAndCheckout(checkin, checkout);
        List<Room> availableRooms = roomService.findAllAvailableRooms(
                checkin,
                checkout,
                hotelName.map(name -> MessageFormat.format("%{0}%", name))
                        .orElse(null),
                null,
                null,
                null);

        return availablePropertiesConverter.convert(availableRooms);
    }

    private static void validateCheckingAndCheckout(LocalDate checkin, LocalDate checkout) {
        if (checkin.isAfter(checkout) || checkin.isEqual(checkout)) {
            throw new ValidationException("Checkin cannot be after checkout");
        }
    }

    public ReservationResponse reserveRoom(ReservationRequest reservationRequest) {
        LocalDate checkIn = reservationRequest.getCheckIn();
        LocalDate checkOut = reservationRequest.getCheckOut();

        validateCheckingAndCheckout(checkIn, checkOut);
        if (roomService.findAllAvailableRooms(
                checkIn,
                checkOut,
                null,
                reservationRequest.getRoomId(),
                null,
                null).isEmpty()) {
            throw new ValidationException("No available rooms for this period");
        }
        return save(reservationRequest);
    }

    @Transactional
    public void cleanUpExpiredReservations() {
        var toBeCleaned = reservationExchangeOnHoldService.findAllExpiredReservations();

        toBeCleaned.forEach(item -> {
            reservationExchangeOnHoldService.delete(item);
            reservationRepository.delete(item.getReservation());
        });
    }

    @Transactional
    public void acceptReservationOnHold(UUID identifier, PaymentRequest paymentRequest) {
        var reservationOnHold = reservationExchangeOnHoldService.findById(identifier);
        reservationExchangeOnHoldService.delete(reservationOnHold);
        reservationRepository.delete(reservationOnHold.getReservation());

        reservationOnHold.getNewReservation().setActive(true);
        reservationOnHold.getNewReservation().setPayment(paymentService.capturePayment(paymentRequest));
        reportReservationToProperty(reservationOnHold.getNewReservation());
        reservationRepository.save(reservationOnHold.getNewReservation());
        counterService.acceptedReservationOnHold(reservationOnHold.getGuest().getId());
    }

    @Transactional
    public void declineReservationOnHold(UUID identifier) {
        var reservationOnHold = reservationExchangeOnHoldService.findById(identifier);
        reservationExchangeOnHoldService.delete(reservationOnHold);
        reservationRepository.delete(reservationOnHold.getNewReservation());
        counterService.declinedReservationOnHold(reservationOnHold.getGuest().getId());
    }

    private void reportReservationToProperty(Reservation reservation) {
        try {
            propertyExternalClient.reportReservationToProperty(reservation);
            reservation.setAcknowledgedByProperty(true);
        } catch (Exception exception) {
            log.error("Impossible to report to property due to Exception", exception);
        }
    }

    public void reportFailedPropertyReports() {
        reservationRepository.findAllByAcknowledgedByPropertyIsFalseAndActiveIsTrue()
                .forEach(this::reportReservationToProperty);
    }

}
