package tn.esprit.autoloc.service;

import java.util.List;

import tn.esprit.autoloc.domain.Maintenance;

public interface IMaintenanceService {

    Maintenance add(Maintenance maintenance);

    Maintenance update(Maintenance maintenance);

    Maintenance getById(Long id);

    List<Maintenance> getAll();

    void delete(Maintenance maintenance);

    void deleteById(Long id);

    boolean existsById(Long id);
}
