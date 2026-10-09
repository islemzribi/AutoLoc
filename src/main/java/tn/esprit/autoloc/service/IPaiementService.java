package tn.esprit.autoloc.service;

import java.util.List;

import tn.esprit.autoloc.domain.Paiement;

public interface IPaiementService {

    Paiement add(Paiement paiement);

    Paiement update(Paiement paiement);

    Paiement getById(Long id);

    List<Paiement> getAll();

    void delete(Paiement paiement);

    void deleteById(Long id);

    boolean existsById(Long id);
}
