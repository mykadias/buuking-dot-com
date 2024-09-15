package br.com.myka.buuking.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse implements BuukingResponse {
    private UUID id;
    private String roomNumber;
    private BigDecimal pricePerNight;
    private UUID propertyId;
}
