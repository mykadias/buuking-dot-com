package br.com.myka.buuking.service;

import br.com.myka.buuking.client.PaymentExternalClient;
import br.com.myka.buuking.converter.PaymentConverter;
import br.com.myka.buuking.entity.Payment;
import br.com.myka.buuking.model.request.PaymentRequest;
import br.com.myka.buuking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentExternalClient paymentExternalClient;
    private final PaymentConverter paymentConverter;
    private final PaymentRepository paymentRepository;

    public final Payment capturePayment(PaymentRequest paymentRequest) {
        var payment = paymentConverter.convert(paymentRequest);

        return capturePayment(payment);
    }

    private Payment capturePayment(Payment payment) {
        try {
            paymentExternalClient.captureMoney(payment);
            payment.setPaid(true);
        } catch (Exception exception) {
            payment.setPaid(false);
            log.error("Impossible to capture money for payment {}. Will be retried later.", payment.getId(), exception);
        }
        return paymentRepository.save(payment);
    }

    public void captureFailedPayments() {
        paymentRepository.findByPaidIsFalse().forEach(this::capturePayment);
    }
}
