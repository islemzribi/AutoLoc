package tn.esprit.autoloc.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Données reçues pour créer ou modifier une maintenance.
 * Le véhicule est référencé uniquement par son identifiant.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRequestDTO {

    @NotNull(message = "La date de début de la maintenance est obligatoire")
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    private String description;

    @NotNull(message = "L'identifiant du véhicule est obligatoire")
    @Positive(message = "L'identifiant du véhicule doit être positif")
    private Long vehiculeId;
}
