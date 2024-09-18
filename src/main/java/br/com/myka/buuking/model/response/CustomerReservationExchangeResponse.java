package br.com.myka.buuking.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerReservationExchangeResponse implements BuukingResponse {
    private UUID reservationExchangeOnHoldId;
    private RoomResponse oldRoom;
    private RoomResponse newRoom;

    private String acceptLink;
    private String declineLink;
    private LocalDate expirationDate;

    private String message;
}

