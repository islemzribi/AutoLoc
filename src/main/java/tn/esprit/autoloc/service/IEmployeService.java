package tn.esprit.autoloc.service;

import java.util.List;

import tn.esprit.autoloc.domain.Employe;

public interface IEmployeService {

    Employe add(Employe employe);

    Employe update(Employe employe);

    Employe getById(Long id);

    List<Employe> getAll();

    void delete(Employe employe);

    void deleteById(Long id);

    boolean existsById(Long id);
}
