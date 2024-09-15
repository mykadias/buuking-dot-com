package br.com.myka.buuking.repository;

import br.com.myka.buuking.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    // Buscar todas as reservas de um quarto que estão em um período específico
//    List<Reservation> findByQuartoIdAndDataSaidaAfterAndDataEntradaBefore(UUID id, LocalDate checkIn, LocalDate checkOut);

//    List<Reservation> findByRoom(UUID id);

}
