package br.com.myka.buuking.service;

import br.com.myka.buuking.converter.AvailablePropertiesConverter;
import br.com.myka.buuking.converter.ReservationConverter;
import br.com.myka.buuking.entity.Room;
import br.com.myka.buuking.exception.ReservationNotFoundException;
import br.com.myka.buuking.exception.ValidationException;
import br.com.myka.buuking.model.request.ReservationRequest;
import br.com.myka.buuking.model.response.AvailablePropertiesResponse;
import br.com.myka.buuking.model.response.ReservationResponse;
import br.com.myka.buuking.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ReservationConverter reservationConverter;

    private final RoomService roomService;

    private final AvailablePropertiesConverter availablePropertiesConverter;

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
        return reservationConverter.convert(reservationRepository.save(reservation));
    }

    public void deleteReservation(UUID id) {
        reservationRepository.deleteById(id);
    }

    @Transactional
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
                reservation.getId()).isEmpty()) {
            throw new ValidationException("No available rooms for this period");
        }
        reservation.setCheckOut(reservationRequest.getCheckOut());
        reservation.setCheckIn(reservationRequest.getCheckIn());
        reservation.setGuestName(reservationRequest.getGuestName());
        return reservationConverter.convert(reservationRepository.save(reservation));
    }


    public AvailablePropertiesResponse findAvailableRoom(LocalDate checkin, LocalDate checkout, Optional<String> hotelName) {
        validateCheckingAndCheckout(checkin, checkout);
        List<Room> availableRooms = roomService.findAllAvailableRooms(
                checkin,
                checkout,
                hotelName.map(name -> MessageFormat.format("%{0}%", name))
                        .orElse(null),
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
                null).isEmpty()) {
            throw new ValidationException("No available rooms for this period");
        }

        return save(reservationRequest);
    }

}
