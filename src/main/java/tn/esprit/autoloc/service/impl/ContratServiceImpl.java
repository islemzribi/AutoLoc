package tn.esprit.autoloc.service.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.service.exception.ResourceNotFoundException;

/**
 * CRUD complet de reference pour l'agrege Contrat (composition des Paiement).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ContratServiceImpl implements IContratService {

    private static final String NOT_FOUND = "Contrat introuvable : ";
    private static final String PAIEMENT_NOT_FOUND = "Paiement introuvable : ";

    private final IContratRepository contratRepository;

    @Override
    public Contrat add(Contrat contrat) {
        return contratRepository.save(contrat);
    }

    @Override
    public Contrat update(Contrat contrat) {
        if (contrat.getIdContrat() == null || !contratRepository.existsById(contrat.getIdContrat())) {
            throw new ResourceNotFoundException(NOT_FOUND + contrat.getIdContrat());
        }
        return contratRepository.save(contrat);
    }

    @Override
    @Transactional(readOnly = true)
    public Contrat getById(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrat> getAll() {
        return contratRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrat> getAllSortedByMontantDesc() {
        return contratRepository.findAll(Sort.by(Sort.Direction.DESC, "montantTotal"));
    }

    @Override
    public Contrat addPaiement(Long contratId, Paiement paiement) {
        Contrat contrat = getById(contratId);
        paiement.setContrat(contrat);
        contrat.getPaiements().add(paiement);
        return contratRepository.save(contrat);
    }

    @Override
    public Contrat removePaiement(Long contratId, Long paiementId) {
        Contrat contrat = getById(contratId);
        Paiement paiement = contrat.getPaiements().stream()
                .filter(p -> paiementId.equals(p.getIdPaiement()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(PAIEMENT_NOT_FOUND + paiementId));
        contrat.getPaiements().remove(paiement);
        return contratRepository.save(contrat);
    }

    @Override
    public void delete(Contrat contrat) {
        contratRepository.delete(contrat);
    }

    @Override
    public void deleteById(Long id) {
        Contrat contrat = getById(id);
        if (contrat.getReservation() != null) {
            contrat.getReservation().setContrat(null);
        }
        contratRepository.delete(contrat);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return contratRepository.existsById(id);
    }
}
