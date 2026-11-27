package tn.esprit.autoloc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Client;

public interface IClientRepository extends JpaRepository<Client, Long> {

    /**
     * Recherche les clients correspondant exactement au nom et au prénom fournis.
     */
    List<Client> findByNomAndPrenom(String nom, String prenom);
}