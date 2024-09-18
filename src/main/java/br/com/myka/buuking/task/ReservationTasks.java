package br.com.myka.buuking.task;

import br.com.myka.buuking.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationTasks {

    private final ReservationService reservationService;

    @Scheduled(fixedDelay = 10000)
    public void reservationOnHoldCleanUpTask() {
        reservationService.cleanUpExpiredReservations();
    }

    @Scheduled(fixedDelay = 10000)
    public void reportReservationNotReportedToProperty() {
        reservationService.reportFailedPropertyReports();
    }

}
