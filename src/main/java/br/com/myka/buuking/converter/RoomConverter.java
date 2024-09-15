package br.com.myka.buuking.converter;

import br.com.myka.buuking.entity.Room;
import br.com.myka.buuking.model.request.RoomRequest;
import br.com.myka.buuking.model.response.RoomResponse;
import org.mapstruct.Mapper;

@Mapper
public interface RoomConverter extends Converter<Room, RoomRequest, RoomResponse> {
}
