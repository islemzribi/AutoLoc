package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;

import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.dto.AgenceRequestDTO;
import tn.esprit.autoloc.dto.AgenceResponseDTO;

/**
 * Conversion entre l'entité Agence et ses DTO.
 */
@Component
public class AgenceMapper {

    public Agence toEntity(AgenceRequestDTO dto) {
        Agence agence = new Agence();
        updateEntity(agence, dto);
        return agence;
    }

    public void updateEntity(Agence agence, AgenceRequestDTO dto) {
        agence.setNom(dto.getNom());
        agence.setVille(dto.getVille());
        agence.setAdresse(dto.getAdresse());
        agence.setTelephone(dto.getTelephone());
    }

    public AgenceResponseDTO toDTO(Agence agence) {
        return AgenceResponseDTO.builder()
                .idAgence(agence.getIdAgence())
                .nom(agence.getNom())
                .ville(agence.getVille())
                .adresse(agence.getAdresse())
                .telephone(agence.getTelephone())
                .build();
    }
}
