package tn.esprit.autoloc.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.dto.VehiculeDTO;

/**
 * Conversion entre l'entité Vehicule et ses DTO.
 * L'agence et les équipements sont gérés par le service, seuls leurs ids sont exposés.
 */
@Component
public class VehiculeMapper {

    public Vehicule toEntity(VehiculeRequestDTO dto) {
        Vehicule vehicule = new Vehicule();
        updateEntity(vehicule, dto);
        return vehicule;
    }

    public void updateEntity(Vehicule vehicule, VehiculeRequestDTO dto) {
        vehicule.setImmatriculation(dto.getImmatriculation());
        vehicule.setMarque(dto.getMarque());
        vehicule.setModele(dto.getModele());
        vehicule.setCategorie(dto.getCategorie());
        vehicule.setTarifJournalier(dto.getTarifJournalier());
        vehicule.setStatut(dto.getStatut());
    }

    public VehiculeDTO toDTO(Vehicule vehicule) {
        Set<Long> equipementIds = vehicule.getEquipements() == null
                ? new HashSet<>()
                : vehicule.getEquipements().stream()
                        .map(equipement -> equipement.getIdEquipement())
                        .collect(Collectors.toSet());

        return VehiculeDTO.builder()
                .idVehicule(vehicule.getIdVehicule())
                .immatriculation(vehicule.getImmatriculation())
                .marque(vehicule.getMarque())
                .modele(vehicule.getModele())
                .categorie(vehicule.getCategorie())
                .tarifJournalier(vehicule.getTarifJournalier())
                .statut(vehicule.getStatut())
                .agenceId(vehicule.getAgence() != null ? vehicule.getAgence().getIdAgence() : null)
                .equipementIds(equipementIds)
                .build();
    }
}
