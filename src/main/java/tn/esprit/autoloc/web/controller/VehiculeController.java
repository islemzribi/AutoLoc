package tn.esprit.autoloc.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.dto.VehiculeDTO;
import tn.esprit.autoloc.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.dto.VehiculeResponseDTO;
import tn.esprit.autoloc.service.IVehiculeService;

/**
 * Endpoints REST de gestion des véhicules (uniquement des DTO en entrée/sortie).
 */
@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @GetMapping
    public List<VehiculeResponseDTO> findAll() {
        return vehiculeService.findAll();
    }

    @GetMapping("/{id}")
    public VehiculeResponseDTO findById(@PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeResponseDTO create(@Valid @RequestBody VehiculeRequestDTO dto) {
        return vehiculeService.create(dto);
    }

    @PutMapping("/{id}")
    public VehiculeResponseDTO update(@PathVariable Long id, @Valid @RequestBody VehiculeRequestDTO dto) {
        return vehiculeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        vehiculeService.deleteById(id);
    }

    @PutMapping("/{idVehicule}/equipements/{idEquipement}")
    public VehiculeDTO affecterVehiculeAEquipement(@PathVariable Long idVehicule,
            @PathVariable Long idEquipement) {
        return vehiculeService.affecterVehiculeAEquipement(idVehicule, idEquipement);
    }

    @PostMapping("/maintenance")
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeDTO affecterMaintenanceAvehicule(@RequestParam String immatriculation,
            @RequestParam String description) {
        return vehiculeService.affecterMaintenanceAvehicule(description, immatriculation);
    }
}
