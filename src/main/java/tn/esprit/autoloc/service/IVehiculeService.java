package tn.esprit.autoloc.service;

import java.util.List;

import tn.esprit.autoloc.domain.Vehicule;

public interface IVehiculeService {

    Vehicule add(Vehicule vehicule);

    Vehicule update(Vehicule vehicule);

    Vehicule getById(Long id);

    List<Vehicule> getAll();

    void delete(Vehicule vehicule);

    void deleteById(Long id);

    boolean existsById(Long id);
}
