package tn.esprit.autoloc.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Données reçues pour créer ou modifier un contrat.
 * La réservation est référencée uniquement par son identifiant.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContratRequestDTO {

    @NotNull(message = "La date de signature est obligatoire")
    @PastOrPresent(message = "La date de signature ne peut pas être dans le futur")
    private LocalDate dateSignature;

    @NotNull(message = "Le montant total est obligatoire")
    @PositiveOrZero(message = "Le montant total doit être positif ou nul")
    private BigDecimal montantTotal;

    private boolean valide;

    @NotNull(message = "L'identifiant de la réservation est obligatoire")
    @Positive(message = "L'identifiant de la réservation doit être positif")
    private Long reservationId;
}
