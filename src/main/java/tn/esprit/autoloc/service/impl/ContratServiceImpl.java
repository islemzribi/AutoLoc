package tn.esprit.autoloc.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.service.IContratService;

@Service
@RequiredArgsConstructor
@Transactional
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;

    @Override
    public Contrat create(Contrat contrat) {
        if (contrat.getIdContrat() != null) {
            throw new IllegalArgumentException("Un nouveau Contrat ne doit pas avoir d'identifiant");
        }
        valider(contrat);
        rattacherPaiements(contrat);
        return contratRepository.save(contrat);
    }

    @Override
    @Transactional(readOnly = true)
    public Contrat findById(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    @Override
    public Contrat update(Long id, Contrat contrat) {
        Contrat existant = findById(id);
        valider(contrat);
        existant.setDateSignature(contrat.getDateSignature());
        existant.setMontantTotal(contrat.getMontantTotal());
        existant.setValide(contrat.isValide());
        existant.setReservation(contrat.getReservation());
        return contratRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        Contrat contrat = findById(id);
        if (contrat.getReservation() != null) {
            contrat.getReservation().setContrat(null);
        }
        contratRepository.delete(contrat);
    }

    private void valider(Contrat contrat) {
        if (contrat.getDateSignature() == null) {
            throw new IllegalArgumentException("La date de signature du contrat est obligatoire");
        }
        if (contrat.getMontantTotal() == null) {
            throw new IllegalArgumentException("Le montant total du contrat est obligatoire");
        }
        if (contrat.getMontantTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant total du contrat ne peut pas etre negatif");
        }
        if (contrat.getReservation() == null) {
            throw new IllegalArgumentException("La reservation associee au contrat est obligatoire");
        }
    }

    private void rattacherPaiements(Contrat contrat) {
        if (contrat.getPaiements() == null) {
            contrat.setPaiements(new ArrayList<>());
            return;
        }
        contrat.getPaiements().forEach(paiement -> paiement.setContrat(contrat));
    }
}
