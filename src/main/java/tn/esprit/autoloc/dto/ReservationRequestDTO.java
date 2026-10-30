package tn.esprit.autoloc.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.StatutReservation;

/**
 * Données reçues pour créer ou modifier une réservation.
 * Le véhicule et le client sont référencés uniquement par leurs identifiants.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDTO {

    @NotNull(message = "La date de début est obligatoire")
    @FutureOrPresent(message = "La date de début ne peut pas être dans le passé")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @FutureOrPresent(message = "La date de fin ne peut pas être dans le passé")
    private LocalDate dateFin;

    @NotNull(message = "Le statut de la réservation est obligatoire")
    private StatutReservation statut;

    @NotNull(message = "L'identifiant du véhicule est obligatoire")
    @Positive(message = "L'identifiant du véhicule doit être positif")
    private Long vehiculeId;

    @NotNull(message = "L'identifiant du client est obligatoire")
    @Positive(message = "L'identifiant du client doit être positif")
    private Long clientId;
}
