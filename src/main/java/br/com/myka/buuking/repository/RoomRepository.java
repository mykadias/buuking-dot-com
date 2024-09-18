package br.com.myka.buuking.repository;

import br.com.myka.buuking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

    @Query("select room from Room room where room.property.id = ?1 and room.roomNumber = ?2")
    List<Room> findRoomsByPropertyIdAndRoomNumber(UUID propertyId, String roomNumber);

    @Query(value = """
            select
                Room.*
            from Room join Property
                on Room.propertyId = Property.id
            where not exists (
                select
                    '*'
                from Reservation
                where Reservation.roomId = Room.id
                    and Reservation.checkIn < :checkOut
                    and Reservation.checkOut > :checkIn
                    and (:reservationId is null or Reservation.id <> :reservationId)
            )
                and (:propertyName is null or UPPER(Property.name) like UPPER(:propertyName))
                and (:roomId is null or Room.id = :roomId)
                and (:propertyId is null or Property.id = :propertyId)
            order by Room.pricePerNight
            """, nativeQuery = true)
    List<Room> findAllAvailableRooms(LocalDate checkIn,
                                     LocalDate checkOut,
                                     String propertyName,
                                     String roomId,
                                     String reservationId,
                                     String propertyId);
}
