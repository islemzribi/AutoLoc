package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

/**
 * Service planifié chargé de clôturer les réservations expirées et de
 * remettre les véhicules associés à disposition.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VehiculeSchedulerService {

    private final IReservationRepository reservationRepository;
    private final IVehiculeRepository vehiculeRepository;

    /**
     * S'exécute tous les jours à 1h00 (cron externalisé dans application.properties).
     */
    @Scheduled(cron = "${scheduler.vehicules.cron}")
    @Transactional
    public void cloturerReservationsExpirees() {
        LocalDate maintenant = LocalDate.now();

        List<Reservation> reservations = reservationRepository
                .findByDateFinBeforeAndStatutNot(maintenant, StatutReservation.TERMINEE);

        if (reservations.isEmpty()) {
            log.info("Aucune réservation expirée à clôturer.");
            return;
        }

        int vehiculesLiberes = 0;
        for (Reservation reservation : reservations) {
            Vehicule vehicule = reservation.getVehicule();

            // Le véhicule redevient disponible
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
            vehiculeRepository.save(vehicule);

            // La réservation est marquée comme terminée
            reservation.setStatut(StatutReservation.TERMINEE);
            reservationRepository.save(reservation);

            vehiculesLiberes++;
            log.info("Réservation #{} clôturée, véhicule #{} ({}) remis à disposition.",
                    reservation.getIdReservation(),
                    vehicule.getIdVehicule(),
                    vehicule.getImmatriculation());
        }

        log.info("{} location(s) clôturée(s) et {} véhicule(s) libéré(s).",
                reservations.size(), vehiculesLiberes);
    }
}
