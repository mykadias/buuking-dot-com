package br.com.myka.buuking.controller.management;

import br.com.myka.buuking.model.request.ReservationRequest;
import br.com.myka.buuking.model.response.ReservationResponse;
import br.com.myka.buuking.service.ReservationService;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
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
@RequestMapping("/management/reservations")
@RequiredArgsConstructor
@Transactional
@Tag(name = "Reservation Management", description = "Operations related to Reservation")
public class ReservationManagementEndpoint implements CrudOperation<ReservationRequest, ReservationResponse, UUID> {

    private final ReservationService reservationService;

    @Override
    @GetMapping
    @Operation(summary = "Get all reservations", description = "Retrieves a list of all reservations.")
    public List<ReservationResponse> findAll() {
        return reservationService.findAll();
    }

    @Override
    @GetMapping("/{identifier}")
    @WithSpan
    @Operation(summary = "Get all Rooms", description = "Retrieves a list of all rooms.")
    public ReservationResponse findById(@SpanAttribute @PathVariable @Valid @NotNull UUID identifier) {
        return reservationService.findReservationById(identifier);
    }

    @Override
    @PostMapping
    @Operation(summary = "Insert a reservation")
    public ReservationResponse insert(@Valid @NotNull @RequestBody ReservationRequest reservationRequest) {
        return reservationService.save(reservationRequest);
    }

    @Override
    @WithSpan
    @PutMapping("/{identifier}")
    @Operation(summary = "Update reservation", description = "Update reservation given its id")
    public ReservationResponse update(@SpanAttribute @PathVariable @Valid @NotNull UUID identifier,
                                      @Valid @NotNull @RequestBody ReservationRequest reservationRequest) {
        return reservationService.update(identifier, reservationRequest);
    }

    @Override
    @WithSpan
    @DeleteMapping("/{identifier}")
    @Operation(summary = "Delete reservation", description = "Delete reservation by its id")
    public void delete(@SpanAttribute @PathVariable @Valid @NotNull UUID identifier) {
        reservationService.deleteReservation(identifier);
    }

}














