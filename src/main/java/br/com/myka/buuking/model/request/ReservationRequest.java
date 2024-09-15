package br.com.myka.buuking.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest implements BuukingRequest {

    @NotBlank
    @Size(min = 1, max = 150, message = "The field must be between 1 and 150 characters long.")
    private String guestName;

    @NotNull
    private LocalDate checkIn;

    @NotNull
    private LocalDate checkOut;

    @NotNull
    private UUID roomId;
}

