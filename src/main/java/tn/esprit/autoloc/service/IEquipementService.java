package tn.esprit.autoloc.service;

import java.util.List;

import tn.esprit.autoloc.domain.Equipement;

public interface IEquipementService {

    Equipement add(Equipement equipement);

    Equipement update(Equipement equipement);

    Equipement getById(Long id);

    List<Equipement> getAll();

    void delete(Equipement equipement);

    void deleteById(Long id);

    boolean existsById(Long id);
}
