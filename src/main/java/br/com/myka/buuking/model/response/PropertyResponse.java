package br.com.myka.buuking.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponse implements BuukingResponse {

    private UUID id;

    private String name;

    private List<RoomResponse> rooms = new ArrayList<>();
}
