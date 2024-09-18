package br.com.myka.buuking.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CounterService {
    private static final String GUEST = "guest";
    private static final String ANONYMOUS = "anonymous";
    private static final String SEARCH_FOR_AVAILABLE_ROOMS = "search.for.available.rooms";
    private static final String RESERVATION = "reservation";
    private static final String DECLINED_RESERVATION_ON_HOLD = "declined.reservation.on.hold";
    private static final String ACCEPTED_RESERVATION_ON_HOLD = "accepted.reservation.on.hold";

    private final MeterRegistry meterRegistry;

    public void availableRoomsSearch(Optional<UUID> guestId) {
        meterRegistry.counter(
                SEARCH_FOR_AVAILABLE_ROOMS,
                GUEST, guestId.map(UUID::toString).orElse(ANONYMOUS))
            .increment();
    }

    public void reservationPerGuest(UUID guestId) {
        meterRegistry.counter(RESERVATION, GUEST, guestId.toString()).increment();
    }

    public void declinedReservationOnHold(UUID guestId) {
        meterRegistry.counter(DECLINED_RESERVATION_ON_HOLD, GUEST, guestId.toString()).increment();
    }

    public void acceptedReservationOnHold(UUID guestId) {
        meterRegistry.counter(ACCEPTED_RESERVATION_ON_HOLD, GUEST, guestId.toString()).increment();
    }
}
