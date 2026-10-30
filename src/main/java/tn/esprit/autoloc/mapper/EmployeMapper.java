package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;

import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.dto.EmployeRequestDTO;
import tn.esprit.autoloc.dto.EmployeResponseDTO;

/**
 * Conversion entre l'entité Employe et ses DTO.
 * L'association Agence est gérée par le service, seule son image (id) est exposée.
 */
@Component
public class EmployeMapper {

    public Employe toEntity(EmployeRequestDTO dto) {
        Employe employe = new Employe();
        updateEntity(employe, dto);
        return employe;
    }

    public void updateEntity(Employe employe, EmployeRequestDTO dto) {
        employe.setNom(dto.getNom());
        employe.setPrenom(dto.getPrenom());
        employe.setRole(dto.getRole());
    }

    public EmployeResponseDTO toDTO(Employe employe) {
        return EmployeResponseDTO.builder()
                .idEmploye(employe.getIdEmploye())
                .nom(employe.getNom())
                .prenom(employe.getPrenom())
                .role(employe.getRole())
                .agenceId(employe.getAgence() != null ? employe.getAgence().getIdAgence() : null)
                .build();
    }
}
