package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;

import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.dto.EquipementRequestDTO;
import tn.esprit.autoloc.dto.EquipementResponseDTO;

/**
 * Conversion entre l'entité Equipement et ses DTO.
 */
@Component
public class EquipementMapper {

    public Equipement toEntity(EquipementRequestDTO dto) {
        Equipement equipement = new Equipement();
        updateEntity(equipement, dto);
        return equipement;
    }

    public void updateEntity(Equipement equipement, EquipementRequestDTO dto) {
        equipement.setLibelle(dto.getLibelle());
    }

    public EquipementResponseDTO toDTO(Equipement equipement) {
        return EquipementResponseDTO.builder()
                .idEquipement(equipement.getIdEquipement())
                .libelle(equipement.getLibelle())
                .build();
    }
}
