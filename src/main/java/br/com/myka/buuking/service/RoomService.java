package br.com.myka.buuking.service;

import br.com.myka.buuking.converter.RoomConverter;
import br.com.myka.buuking.entity.Room;
import br.com.myka.buuking.exception.RoomNotFoundException;
import br.com.myka.buuking.exception.ValidationException;
import br.com.myka.buuking.model.request.RoomRequest;
import br.com.myka.buuking.model.response.RoomResponse;
import br.com.myka.buuking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final PropertyService propertyService;
    private final RoomConverter roomConverter;

    public List<RoomResponse> getAll() {
        return roomRepository.findAll().stream().map(roomConverter::convert).toList();
    }

    public RoomResponse getRoomById(UUID id) {
        return roomRepository.findById(id).map(roomConverter::convert)
                .orElseThrow(() -> new RoomNotFoundException(id));
    }

    public RoomResponse save(RoomRequest roomRequest) {
        var room = roomConverter.convert(roomRequest);
        room.setProperty(propertyService.getProperty(room.getPropertyId()));
        if (!roomRepository.findRoomsByPropertyIdAndRoomNumber(room.getPropertyId(), room.getRoomNumber()).isEmpty()) {
            throw new ValidationException("Room number already exists");
        }
        return roomConverter.convert(roomRepository.save(room));
    }

    public RoomResponse update(UUID id, RoomRequest roomRequest) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RoomNotFoundException(id));
        room.setRoomNumber(roomRequest.getRoomNumber());
        room.setPricePerNight(roomRequest.getPricePerNight());

        if (roomRepository.findRoomsByPropertyIdAndRoomNumber(room.getPropertyId(), room.getRoomNumber())
                .stream()
                .anyMatch(item -> !item.getId().equals(id))) {
            throw new ValidationException("Room number already exists");
        }
        return roomConverter.convert(roomRepository.save(room));
    }

    public void deleteRoom(UUID id) {
        roomRepository.deleteById(id);
    }

    public Room getRoom(UUID roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException(roomId));
    }

    public List<Room> findAllAvailableRoomsInAnyProperty(LocalDate checkIn, LocalDate checkOut){
        return findAllAvailableRooms(checkIn, checkOut, null, null, null, null);
    }

    public List<Room> findAllAvailableRooms(LocalDate checkIn,
                                            LocalDate checkOut,
                                            String hotelName,
                                            UUID roomId,
                                            UUID reservationId,
                                            UUID propertyId) {
        return roomRepository.findAllAvailableRooms(checkIn,
                checkOut,
                hotelName,
                Optional.ofNullable(roomId)
                        .map(UUID::toString)
                        .orElse(null),
                Optional.ofNullable(reservationId)
                        .map(UUID::toString)
                        .orElse(null),
                Optional.ofNullable(propertyId)
                        .map(UUID::toString)
                        .orElse(null)
        );
    }

    public RoomResponse convertToRoomResponse(Room room){
        return roomConverter.convert(room);
    }
}
