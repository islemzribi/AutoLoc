package tn.esprit.autoloc.service;

import java.util.List;

import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;

public interface IContratService {

    Contrat add(Contrat contrat);

    Contrat update(Contrat contrat);

    Contrat getById(Long id);

    List<Contrat> getAll();

    List<Contrat> getAllSortedByMontantDesc();

    Contrat addPaiement(Long contratId, Paiement paiement);

    Contrat removePaiement(Long contratId, Long paiementId);

    void delete(Contrat contrat);

    void deleteById(Long id);

    boolean existsById(Long id);
}
