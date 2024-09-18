package br.com.myka.buuking.controller.operational;

import br.com.myka.buuking.model.request.PaymentRequest;
import br.com.myka.buuking.model.response.CustomerReservationExchangeResponse;
import br.com.myka.buuking.service.ReservationExchangeOnHoldService;
import br.com.myka.buuking.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reservation-on-hold")
@RequiredArgsConstructor
@Tag(name = "Reservation Exchange on Hold", description = "Operations related to Reservations Exchange on hold for customers")
@Transactional
public class ReservationOnHoldOperationsEndpoint {

    private final ReservationExchangeOnHoldService reservationExchangeOnHoldService;

    private final ReservationService reservationService;

    @GetMapping
    @Operation(summary = "Find all Reservation on hold for a customer")
    public List<CustomerReservationExchangeResponse> findAvailableRooms(@RequestHeader UUID guestId) {
        return reservationExchangeOnHoldService.findAllByGuestId(guestId);
    }

    @PutMapping("/accept/{identifier}")
    @Operation(summary = "Accept reservation on hold")
    public void accept(@PathVariable UUID identifier, @RequestBody @Valid @NotNull PaymentRequest paymentRequest) {
        reservationService.acceptReservationOnHold(identifier, paymentRequest);
    }

    @DeleteMapping("/decline/{identifier}")
    @Operation(summary = "Decline a reservation on hold")
    public void cancelReservation(@PathVariable UUID identifier) {
        reservationService.declineReservationOnHold(identifier);
    }
}
