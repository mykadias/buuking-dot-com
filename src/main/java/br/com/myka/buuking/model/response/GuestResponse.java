package br.com.myka.buuking.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestResponse implements BuukingResponse {

    private UUID id;

    private String name;
}
