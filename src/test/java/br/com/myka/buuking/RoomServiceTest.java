package br.com.myka.buuking;

import br.com.myka.buuking.converter.RoomConverter;
import br.com.myka.buuking.entity.Property;
import br.com.myka.buuking.entity.Room;
import br.com.myka.buuking.exception.RoomNotFoundException;
import br.com.myka.buuking.exception.ValidationException;
import br.com.myka.buuking.model.request.RoomRequest;
import br.com.myka.buuking.model.response.RoomResponse;
import br.com.myka.buuking.repository.RoomRepository;
import br.com.myka.buuking.service.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @InjectMocks
    private RoomService roomService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomConverter roomConverter;



    @Test
    void getAll_shouldReturnAllRoomResponses() {
        // arrange
        UUID propertyId = UUID.randomUUID();
        Property property = new Property(propertyId, "Arkham Hotel", List.of());
        Room room = new Room(UUID.randomUUID(), "101", BigDecimal.valueOf(100.0), property, UUID.randomUUID());
        RoomResponse roomResponse = new RoomResponse(room.getId(), room.getRoomNumber(), room.getPricePerNight(), propertyId);

        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(roomConverter.convert(room)).thenReturn(roomResponse);

        // act
        List<RoomResponse> result = roomService.getAll();

        // assert
        assertThat(result).isEqualTo(List.of(roomResponse));
        verify(roomRepository).findAll();
        verify(roomConverter).convert(room);
    }

    @Test
    void getRoomById_shouldReturnRoomResponse() {
        // arrange
        UUID roomId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        Property property = new Property(propertyId, "Arkham Hotel", List.of());
        Room room = new Room(UUID.randomUUID(), "101", BigDecimal.valueOf(100.0), property, UUID.randomUUID());
        RoomResponse roomResponse = new RoomResponse(room.getId(), room.getRoomNumber(), room.getPricePerNight(), propertyId);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomConverter.convert(room)).thenReturn(roomResponse);

        // act
        RoomResponse result = roomService.getRoomById(roomId);

        // assert
        assertThat(result).isEqualTo(roomResponse);
        verify(roomRepository).findById(roomId);
        verify(roomConverter).convert(room);
    }


    @Test
    void getRoomById_whenNotFound_shouldThrowRoomNotFoundException() {
        // arrange
        UUID roomId = UUID.randomUUID();

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(RoomNotFoundException.class, () -> roomService.getRoomById(roomId));
        verify(roomRepository).findById(roomId);
    }

    @Test
    void update_whenRoomNumberExists_shouldThrowValidationException() {
        // arrange
        UUID roomId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        Property property = new Property(propertyId, "Gotham Hotel", List.of());
        RoomRequest roomRequest = new RoomRequest("102", BigDecimal.valueOf(150.0), UUID.randomUUID());
        Room existingRoom = new Room(UUID.randomUUID(), "101", BigDecimal.valueOf(100.0), property, UUID.randomUUID());
        Room updatedRoom = new Room(UUID.randomUUID(), "102", BigDecimal.valueOf(150.0), property, UUID.randomUUID());

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(existingRoom));
        when(roomRepository.findRoomsByPropertyIdAndRoomNumber(any(), any())).thenReturn(List.of(updatedRoom));

        // act & assert
        assertThrows(ValidationException.class, () -> roomService.update(roomId, roomRequest));
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void findAllAvailableRooms_shouldReturnAvailableRooms() {
        // arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);
        UUID propertyId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        Property property = new Property(propertyId, "Springfield Hotel", List.of());
        Room room = new Room(UUID.randomUUID(), "101", BigDecimal.valueOf(100.0), property, UUID.randomUUID());

        when(roomRepository.findAllAvailableRooms(checkIn, checkOut, null, roomId.toString(), reservationId.toString(), propertyId.toString()))
            .thenReturn(List.of(room));

        // act
        List<Room> result = roomService.findAllAvailableRooms(checkIn, checkOut, null, roomId, reservationId, propertyId);

        // assert
        assertThat(result).isEqualTo(List.of(room));
        verify(roomRepository).findAllAvailableRooms(checkIn, checkOut, null, roomId.toString(), reservationId.toString(), propertyId.toString());
    }

    @Test
    void deleteRoom_shouldCallRepositoryDeleteById() {
        // arrange
        UUID roomId = UUID.randomUUID();

        // act
        roomService.deleteRoom(roomId);

        // assert
        verify(roomRepository).deleteById(roomId);
    }
}

