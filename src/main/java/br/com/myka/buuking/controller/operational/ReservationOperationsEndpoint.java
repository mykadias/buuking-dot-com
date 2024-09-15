package br.com.myka.buuking.controller.operational;

import br.com.myka.buuking.model.request.ReservationRequest;
import br.com.myka.buuking.model.response.AvailablePropertiesResponse;
import br.com.myka.buuking.model.response.ReservationResponse;
import br.com.myka.buuking.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
@Tag(name = "Reservation", description = "Operations related to Reservations for customers")
@Transactional
public class ReservationOperationsEndpoint {

    private final ReservationService reservationService;

    @GetMapping("/find-available-room")
    @Operation(summary = "Find all available rooms",
            description = "Find all available rooms by checkin, checkout and an optional hotelName")
    public AvailablePropertiesResponse findAvailableRooms(@RequestParam LocalDate checkin,
                                                          @RequestParam LocalDate checkout,
                                                          @RequestParam(required = false) Optional<String> hotelName) {
        return reservationService.findAvailableRoom(checkin, checkout, hotelName);
    }

    @PostMapping("/reserve-room")
    @Operation(summary = "Reserve a room")
    public ReservationResponse reserveRoom(@Valid @RequestBody ReservationRequest reservationRequest) {
        return reservationService.reserveRoom(reservationRequest);
    }

    @DeleteMapping("/cancel-reservation/{identifier}")
    @Operation(summary = "Cancel reservation", description = "Cancel a reservation by id for a room")
    public void cancelReservation(@PathVariable @Valid @NotNull UUID identifier) {
        reservationService.deleteReservation(identifier);
    }

    @PutMapping("/update-reservation/{identifier}")
    @Operation(summary = "Update a reservation", description = "Update a reservation by id for a room")
    public ReservationResponse updateReservation(@PathVariable @Valid @NotNull UUID identifier,
                                                 @RequestBody @Valid @NotNull ReservationRequest reservationRequest) {
        return reservationService.update(identifier, reservationRequest);
    }
}
