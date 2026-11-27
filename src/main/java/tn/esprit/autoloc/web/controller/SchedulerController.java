package tn.esprit.autoloc.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.service.ContratSchedulerService;
import tn.esprit.autoloc.service.VehiculeSchedulerService;

/**
 * Endpoints de déclenchement manuel des tâches planifiées.
 * Utile pour tester les schedulers depuis Swagger sans attendre le cron.
 * À retirer ou sécuriser en production.
 */
@RestController
@RequestMapping("/schedulers")
@RequiredArgsConstructor
public class SchedulerController {

    private final ContratSchedulerService contratSchedulerService;
    private final VehiculeSchedulerService vehiculeSchedulerService;

    /**
     * Déclenche immédiatement la vérification des contrats arrivant à échéance sous 48 h.
     */
    @PostMapping("/contrats/run")
    public ResponseEntity<String> lancerContrats() {
        contratSchedulerService.verifierContratsEcheanceProche();
        return ResponseEntity.ok("Tâche des contrats exécutée. Consultez les logs de l'application.");
    }

    /**
     * Déclenche immédiatement la clôture des réservations expirées et la libération des véhicules.
     */
    @PostMapping("/vehicules/run")
    public ResponseEntity<String> lancerVehicules() {
        vehiculeSchedulerService.cloturerReservationsExpirees();
        return ResponseEntity.ok("Tâche des véhicules exécutée. Consultez les logs de l'application.");
    }
}
