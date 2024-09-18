package br.com.myka.buuking.converter;

import br.com.myka.buuking.entity.ReservationExchangeOnHold;
import br.com.myka.buuking.entity.Room;
import br.com.myka.buuking.model.response.CustomerReservationExchangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerReservationExchangeConverter {

    private static final String DIFFERENT_ROOM_FOUND_MESSAGE = "Same room is not available, but we found a room in the same property for you.";
    private static final String NO_ROOM_AVAILABLE_IN_THE_PROPERTY = "No room is available in this property for the requested period. We selected another room in a different property. Please, check it out";
    private static final String ON_HOLD_RESERVATION_EXCHANGE_URL = "localhost:8080/reservation-on-hold/";
    private static final String ACCEPT_ON_HOLD_RESERVATION_EXCHANGE_URL = ON_HOLD_RESERVATION_EXCHANGE_URL.concat("accept/%s");
    private static final String DECLINE_ON_HOLD_RESERVATION_EXCHANGE_URL = ON_HOLD_RESERVATION_EXCHANGE_URL.concat("decline/%s");

    private final RoomConverter roomConverter;

    public CustomerReservationExchangeResponse convert(ReservationExchangeOnHold reservationExchangeOnHold) {
        Room room = reservationExchangeOnHold.getReservation().getRoom();
        Room newRoom = reservationExchangeOnHold.getNewReservation().getRoom();

        return CustomerReservationExchangeResponse.builder()
            .acceptLink(ACCEPT_ON_HOLD_RESERVATION_EXCHANGE_URL.formatted(reservationExchangeOnHold.getId()))
            .declineLink(DECLINE_ON_HOLD_RESERVATION_EXCHANGE_URL.formatted(reservationExchangeOnHold.getId()))
            .expirationDate(reservationExchangeOnHold.getExpirationDate().toLocalDate())
            .reservationExchangeOnHoldId(reservationExchangeOnHold.getId())
            .oldRoom(roomConverter.convert(room))
            .newRoom(roomConverter.convert(newRoom))
            .message(defineMessage(room, newRoom))
            .build();
    }

    private String defineMessage(Room room, Room newRoom) {
        return room.getPropertyId().equals(newRoom.getPropertyId())
            ? DIFFERENT_ROOM_FOUND_MESSAGE
            : NO_ROOM_AVAILABLE_IN_THE_PROPERTY;
    }
}
