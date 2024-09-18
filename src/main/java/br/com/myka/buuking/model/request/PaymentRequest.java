package br.com.myka.buuking.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest implements BuukingRequest {
    @NotBlank
    @Size(min = 1, max = 16, message = "The field must be between 1 and 150 characters long.")
    private String creditCardNumber;
}