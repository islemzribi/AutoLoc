package tn.esprit.autoloc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.RoleEmploye;

/**
 * Représentation exposée d'un employé (l'agence est réduite à son identifiant).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeResponseDTO {

    private Long idEmploye;
    private String nom;
    private String prenom;
    private RoleEmploye role;
    private Long agenceId;
}
