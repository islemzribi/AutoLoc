package tn.esprit.autoloc.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.esprit.autoloc.domain.Contrat;

public interface IContratRepository extends JpaRepository<Contrat, Long> {

    /**
     * Récupère les contrats dont la date de fin de la réservation associée
     * est comprise entre les deux dates fournies (bornes incluses).
     */
    List<Contrat> findByReservationDateFinBetween(LocalDate debut, LocalDate fin);
}