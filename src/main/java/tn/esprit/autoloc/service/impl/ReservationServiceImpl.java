package tn.esprit.autoloc.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.service.IReservationService;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;

    @Override
    public Reservation create(Reservation reservation) {
        if (reservation.getIdReservation() != null) {
            throw new IllegalArgumentException("Un nouveau Reservation ne doit pas avoir d'identifiant");
        }
        valider(reservation);
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation update(Long id, Reservation reservation) {
        Reservation existant = findById(id);
        valider(reservation);
        existant.setDateDebut(reservation.getDateDebut());
        existant.setDateFin(reservation.getDateFin());
        existant.setStatut(reservation.getStatut());
        existant.setVehicule(reservation.getVehicule());
        existant.setClient(reservation.getClient());
        return reservationRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation", id);
        }
        reservationRepository.deleteById(id);
    }

    private void valider(Reservation reservation) {
        if (reservation.getDateDebut() == null) {
            throw new IllegalArgumentException("La date de debut de la reservation est obligatoire");
        }
        if (reservation.getDateFin() == null) {
            throw new IllegalArgumentException("La date de fin de la reservation est obligatoire");
        }
        if (reservation.getDateFin().isBefore(reservation.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas etre anterieure a la date de debut");
        }
        if (reservation.getStatut() == null) {
            throw new IllegalArgumentException("Le statut de la reservation est obligatoire");
        }
        if (reservation.getVehicule() == null) {
            throw new IllegalArgumentException("Le vehicule de la reservation est obligatoire");
        }
        if (reservation.getClient() == null) {
            throw new IllegalArgumentException("Le client de la reservation est obligatoire");
        }
    }
}
