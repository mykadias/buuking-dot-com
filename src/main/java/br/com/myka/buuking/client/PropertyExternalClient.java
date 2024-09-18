package br.com.myka.buuking.client;

import br.com.myka.buuking.entity.Reservation;
import br.com.myka.buuking.exception.ConnectionResetByPeerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulates an external api call, which can fail.
 */
@Component
@Slf4j
public class PropertyExternalClient {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private static final int FACTOR = 4;

    public void reportReservationToProperty(Reservation reservation) {
        if (COUNTER.getAndIncrement() % FACTOR == 0) {
            throw new ConnectionResetByPeerException();
        }
        log.info("Reservation {} reported to property {}", reservation.getId(), reservation.getRoom().getPropertyId());
    }

}
