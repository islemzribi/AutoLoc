package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;

import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.dto.PaiementRequestDTO;
import tn.esprit.autoloc.dto.PaiementResponseDTO;

/**
 * Conversion entre l'entité Paiement et ses DTO.
 * Le contrat est géré par le service, seul son id est exposé.
 */
@Component
public class PaiementMapper {

    public Paiement toEntity(PaiementRequestDTO dto) {
        Paiement paiement = new Paiement();
        updateEntity(paiement, dto);
        return paiement;
    }

    public void updateEntity(Paiement paiement, PaiementRequestDTO dto) {
        paiement.setMontant(dto.getMontant());
        paiement.setDatePaiement(dto.getDatePaiement());
        paiement.setModePaiement(dto.getModePaiement());
    }

    public PaiementResponseDTO toDTO(Paiement paiement) {
        return PaiementResponseDTO.builder()
                .idPaiement(paiement.getIdPaiement())
                .montant(paiement.getMontant())
                .datePaiement(paiement.getDatePaiement())
                .modePaiement(paiement.getModePaiement())
                .contratId(paiement.getContrat() != null ? paiement.getContrat().getIdContrat() : null)
                .build();
    }
}
