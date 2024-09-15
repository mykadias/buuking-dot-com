package br.com.myka.buuking.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse implements BuukingResponse {
    private UUID id;
    private UUID roomId;
    private String guestName;
    private LocalDate checkIn;
    private LocalDate checkOut;
}

