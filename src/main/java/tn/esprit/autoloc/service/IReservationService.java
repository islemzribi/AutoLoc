package tn.esprit.autoloc.service;

import java.util.List;

import tn.esprit.autoloc.domain.Reservation;

public interface IReservationService {

    Reservation add(Reservation reservation);

    Reservation update(Reservation reservation);

    Reservation getById(Long id);

    List<Reservation> getAll();

    void delete(Reservation reservation);

    void deleteById(Long id);

    boolean existsById(Long id);
}
