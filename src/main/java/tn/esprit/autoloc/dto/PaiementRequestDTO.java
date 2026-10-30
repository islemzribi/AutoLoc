package tn.esprit.autoloc.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.ModePaiement;

/**
 * Données reçues pour créer ou modifier un paiement.
 * Le contrat est référencé uniquement par son identifiant.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaiementRequestDTO {

    @NotNull(message = "Le montant du paiement est obligatoire")
    @Positive(message = "Le montant du paiement doit être strictement positif")
    private BigDecimal montant;

    @NotNull(message = "La date de paiement est obligatoire")
    @PastOrPresent(message = "La date de paiement ne peut pas être dans le futur")
    private LocalDate datePaiement;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiement modePaiement;

    @NotNull(message = "L'identifiant du contrat est obligatoire")
    @Positive(message = "L'identifiant du contrat doit être positif")
    private Long contratId;
}
