package tn.esprit.autoloc.service;

import java.util.List;

import tn.esprit.autoloc.domain.Agence;

public interface IAgenceService {

    Agence add(Agence agence);

    Agence update(Agence agence);

    Agence getById(Long id);

    List<Agence> getAll();

    void delete(Agence agence);

    void deleteById(Long id);

    boolean existsById(Long id);
}
