package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;

import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.dto.ReservationRequestDTO;
import tn.esprit.autoloc.dto.ReservationResponseDTO;

/**
 * Conversion entre l'entité Reservation et ses DTO.
 * Le véhicule et le client sont gérés par le service, seuls leurs ids sont exposés.
 */
@Component
public class ReservationMapper {

    public Reservation toEntity(ReservationRequestDTO dto) {
        Reservation reservation = new Reservation();
        updateEntity(reservation, dto);
        return reservation;
    }

    public void updateEntity(Reservation reservation, ReservationRequestDTO dto) {
        reservation.setDateDebut(dto.getDateDebut());
        reservation.setDateFin(dto.getDateFin());
        reservation.setStatut(dto.getStatut());
    }

    public ReservationResponseDTO toDTO(Reservation reservation) {
        return ReservationResponseDTO.builder()
                .idReservation(reservation.getIdReservation())
                .dateDebut(reservation.getDateDebut())
                .dateFin(reservation.getDateFin())
                .statut(reservation.getStatut())
                .vehiculeId(reservation.getVehicule() != null ? reservation.getVehicule().getIdVehicule() : null)
                .clientId(reservation.getClient() != null ? reservation.getClient().getIdClient() : null)
                .build();
    }
}
