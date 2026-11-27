package tn.esprit.autoloc.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Récupère les réservations dont la date de fin est antérieure à la date
     * fournie et dont le statut est différent de celui passé en paramètre.
     */
    List<Reservation> findByDateFinBeforeAndStatutNot(LocalDate date, StatutReservation statut);

    /**
     * Récupère les réservations qui ne sont encore liées à aucun contrat.
     */
    List<Reservation> findAllByContratIsNull();
}