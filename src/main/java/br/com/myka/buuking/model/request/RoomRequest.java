package br.com.myka.buuking.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest implements BuukingRequest {

    @NotBlank
    @Size(min = 1, max = 10, message = "The field must be between 1 and 10 characters long.")
    private String roomNumber;

    @NotNull
    @Positive
    private BigDecimal pricePerNight;

    @NotNull
    private UUID propertyId;
}
