package tn.esprit.autoloc.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.dto.ContratDTO;
import tn.esprit.autoloc.dto.ContratRequestDTO;
import tn.esprit.autoloc.dto.ContratResponseDTO;

/**
 * Conversion entre l'entité Contrat et ses DTO.
 * La réservation et les paiements sont exposés uniquement par leurs identifiants.
 */
@Component
public class ContratMapper {

    public Contrat toEntity(ContratRequestDTO dto) {
        Contrat contrat = new Contrat();
        updateEntity(contrat, dto);
        return contrat;
    }

    public void updateEntity(Contrat contrat, ContratRequestDTO dto) {
        contrat.setDateSignature(dto.getDateSignature());
        contrat.setMontantTotal(dto.getMontantTotal());
        contrat.setValide(dto.isValide());
    }

    public ContratResponseDTO toDTO(Contrat contrat) {
        List<Long> paiementIds = contrat.getPaiements() == null
                ? new ArrayList<>()
                : contrat.getPaiements().stream()
                        .map(paiement -> paiement.getIdPaiement())
                        .toList();

        return ContratResponseDTO.builder()
                .idContrat(contrat.getIdContrat())
                .dateSignature(contrat.getDateSignature())
                .montantTotal(contrat.getMontantTotal())
                .valide(contrat.isValide())
                .reservationId(contrat.getReservation() != null ? contrat.getReservation().getIdReservation() : null)
                .paiementIds(paiementIds)
                .build();
    }

    /**
     * Variante utilisée par les services d'affectation retournant un ContratDTO.
     */
    public ContratDTO toContratDTO(Contrat contrat) {
        List<Long> paiementIds = contrat.getPaiements() == null
                ? new ArrayList<>()
                : contrat.getPaiements().stream()
                        .map(paiement -> paiement.getIdPaiement())
                        .toList();

        return ContratDTO.builder()
                .idContrat(contrat.getIdContrat())
                .dateSignature(contrat.getDateSignature())
                .montantTotal(contrat.getMontantTotal())
                .valide(contrat.isValide())
                .reservationId(contrat.getReservation() != null ? contrat.getReservation().getIdReservation() : null)
                .paiementIds(paiementIds)
                .build();
    }
}
