package br.com.myka.buuking.configuration.micrometer;

import br.com.myka.buuking.repository.PaymentRepository;
import br.com.myka.buuking.repository.ReservationExchangeOnHoldRepository;
import br.com.myka.buuking.repository.ReservationRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeterConfiguration {

    private static final String RESERVATION_NOT_PAID = "reservation.not.paid";
    private static final String RESERVATION_NOT_ACKNOWLEDGED = "reservation.not.acknowledged";
    private static final String RESERVATION_EXCHANGE_ON_HOLD = "reservation.exchange.on.hold";

    @Bean
    public MeterBinder reportNotPaidReservation(PaymentRepository paymentRepository) {
        return registry ->
            Gauge.builder(RESERVATION_NOT_PAID, () -> paymentRepository.findByPaidIsFalse().size())
                .register(registry);
    }

    @Bean
    public MeterBinder reportReservationNotAcknowledgedByProperty(ReservationRepository reservationRepository) {
        return registry ->
            Gauge.builder(RESERVATION_NOT_ACKNOWLEDGED,
                    () -> reservationRepository.findAllByAcknowledgedByPropertyIsFalseAndActiveIsTrue().size())
                .register(registry);
    }

    @Bean
    public MeterBinder reservationExchangeOnHold(ReservationExchangeOnHoldRepository reservationExchangeOnHoldRepository) {
        return registry ->
            Gauge.builder(RESERVATION_EXCHANGE_ON_HOLD, reservationExchangeOnHoldRepository::count)
                .register(registry);
    }


}
