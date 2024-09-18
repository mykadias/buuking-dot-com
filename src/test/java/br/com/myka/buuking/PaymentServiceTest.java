package br.com.myka.buuking;

import br.com.myka.buuking.client.PaymentExternalClient;
import br.com.myka.buuking.converter.PaymentConverter;
import br.com.myka.buuking.entity.Payment;
import br.com.myka.buuking.model.request.PaymentRequest;
import br.com.myka.buuking.repository.PaymentRepository;
import br.com.myka.buuking.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentExternalClient paymentExternalClient;

    @Mock
    private PaymentConverter paymentConverter;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void capturePayment_shouldCapturePaymentSuccessfully() {
        // arrange
        UUID paymentId = UUID.randomUUID();
        PaymentRequest paymentRequest = new PaymentRequest();
        Payment payment = new Payment(paymentId, "12546304", false);
        Payment savedPayment = new Payment(paymentId, "12546399", true);

        when(paymentConverter.convert(paymentRequest)).thenReturn(payment);
        doNothing().when(paymentExternalClient).captureMoney(payment);
        when(paymentRepository.save(payment)).thenReturn(savedPayment);

        // act
        Payment result = paymentService.capturePayment(paymentRequest);

        // assert
        assertThat(result.isPaid()).isTrue();
        verify(paymentExternalClient).captureMoney(payment);
        verify(paymentRepository).save(payment);
    }

    @Test
    void capturePayment_whenExternalClientFails_shouldSetPaymentAsNotPaid() {
        // arrange
        UUID paymentId = UUID.randomUUID();
        PaymentRequest paymentRequest = new PaymentRequest();
        Payment payment = new Payment(paymentId, "5469817", false);

        when(paymentConverter.convert(paymentRequest)).thenReturn(payment);
        doThrow(new RuntimeException("External service error")).when(paymentExternalClient).captureMoney(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);

        // act
        Payment result = paymentService.capturePayment(paymentRequest);

        // assert
        assertThat(result.isPaid()).isFalse();
        verify(paymentExternalClient).captureMoney(payment);
        verify(paymentRepository).save(payment);
    }

    @Test
    void captureFailedPayments_whenNoFailedPayments_shouldNotAttemptCapture() {
        // arrange
        when(paymentRepository.findByPaidIsFalse()).thenReturn(List.of());

        // act
        paymentService.captureFailedPayments();

        // assert
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentExternalClient, never()).captureMoney(any(Payment.class));
    }
}
