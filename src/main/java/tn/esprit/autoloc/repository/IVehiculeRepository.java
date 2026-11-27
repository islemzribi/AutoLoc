package tn.esprit.autoloc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Vehicule;

public interface IVehiculeRepository extends JpaRepository<Vehicule, Long> {

    /**
     * Recherche un véhicule par son immatriculation (unique).
     */
    Optional<Vehicule> findByImmatriculation(String immatriculation);
}