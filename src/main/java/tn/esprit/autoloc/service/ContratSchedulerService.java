package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.repository.IContratRepository;

/**
 * Service planifié chargé de signaler les contrats dont la location arrive
 * à échéance dans les 48 prochaines heures.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContratSchedulerService {

    private final IContratRepository contratRepository;

    /**
     * S'exécute du lundi au vendredi à 7h00 (cron externalisé dans application.properties).
     * La lecture transactionnelle permet d'accéder aux associations LAZY
     * (réservation et client) sans provoquer de LazyInitializationException.
     */
    @Scheduled(cron = "${scheduler.contrats.cron}")
    @Transactional(readOnly = true)
    public void verifierContratsEcheanceProche() {
        LocalDate maintenant = LocalDate.now();
        LocalDate dans48Heures = maintenant.plusDays(2);

        List<Contrat> contrats = contratRepository.findByReservationDateFinBetween(maintenant, dans48Heures);

        if (contrats.isEmpty()) {
            log.info("Aucun contrat n'arrive à échéance dans les 48 prochaines heures.");
            return;
        }

        log.info("{} contrat(s) arrivent à échéance dans les 48 prochaines heures.", contrats.size());
        for (Contrat contrat : contrats) {
            Reservation reservation = contrat.getReservation();
            Client client = reservation.getClient();
            log.info("Contrat #{} : client {} {} (réservation #{}) - date de fin : {}",
                    contrat.getIdContrat(),
                    client.getNom(),
                    client.getPrenom(),
                    reservation.getIdReservation(),
                    reservation.getDateFin());
        }
    }
}
