package br.com.myka.buuking.converter;

import br.com.myka.buuking.entity.Room;
import br.com.myka.buuking.model.response.AvailablePropertiesResponse;
import br.com.myka.buuking.model.response.PropertyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AvailablePropertiesConverter {

    private final PropertyConverter propertyConverter;
    private final RoomConverter roomConverter;

    public AvailablePropertiesResponse convert(List<Room> rooms) {
        Map<UUID, PropertyResponse> properties = new HashMap<>();
        rooms.forEach(room ->
                properties.compute(
                        room.getPropertyId(),
                        (key, value) -> {
                            if (value == null) {
                                value = propertyConverter.convert(room.getProperty());
                                value.getRooms().clear();
                            }
                            value.getRooms().add(roomConverter.convert(room));
                            return value;
                        })
        );

        return AvailablePropertiesResponse.builder().properties(properties.values().stream().toList()).build();
    }

}
