package tn.esprit.autoloc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Données reçues pour créer ou modifier une agence.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgenceRequestDTO {

    @NotBlank(message = "Le nom de l'agence est obligatoire")
    @Size(max = 100, message = "Le nom de l'agence ne doit pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "La ville de l'agence est obligatoire")
    @Size(max = 50, message = "La ville ne doit pas dépasser 50 caractères")
    private String ville;

    @NotBlank(message = "L'adresse de l'agence est obligatoire")
    @Size(max = 150, message = "L'adresse ne doit pas dépasser 150 caractères")
    private String adresse;

    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    private String telephone;
}
