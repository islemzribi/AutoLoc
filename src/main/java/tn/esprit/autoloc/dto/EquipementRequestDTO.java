package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Données reçues pour créer ou modifier un équipement.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipementRequestDTO {

    @NotBlank(message = "Le libellé de l'équipement est obligatoire")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères")
    private String libelle;
}
