package tn.esprit.autoloc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Représentation exposée d'une agence.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgenceResponseDTO {

    private Long idAgence;
    private String nom;
    private String ville;
    private String adresse;
    private String telephone;
}
