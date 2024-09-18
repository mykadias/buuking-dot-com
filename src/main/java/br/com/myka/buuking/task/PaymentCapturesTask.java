package br.com.myka.buuking.task;

import br.com.myka.buuking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCapturesTask {

    private final PaymentService paymentService;

    @Scheduled(fixedDelay = 10000)
    public void captureFailedPayments() {
        paymentService.captureFailedPayments();
    }

}
