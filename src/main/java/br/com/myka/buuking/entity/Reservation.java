package br.com.myka.buuking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reservation implements BuukingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.CHAR)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "roomId", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate checkIn;

    @Column(nullable = false)
    private LocalDate checkOut;

    @ManyToOne
    @JoinColumn(name = "guestId", nullable = false)
    private Guest guest;

    @OneToOne
    @JoinColumn(name = "paymentId")
    private Payment payment;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean acknowledgedByProperty;

    @Transient
    private UUID roomId;

    @Transient
    private UUID guestId;

    public UUID getRoomId() {
        if (roomId == null) {
            roomId = room.getId();
        }
        return roomId;
    }

    public UUID getGuestId() {
        if (guestId == null) {
            guestId = guest.getId();
        }
        return guestId;
    }

}
