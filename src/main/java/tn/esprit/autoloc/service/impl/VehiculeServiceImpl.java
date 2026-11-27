package tn.esprit.autoloc.service.impl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.VehiculeDTO;
import tn.esprit.autoloc.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.dto.VehiculeResponseDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.VehiculeMapper;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.service.IVehiculeService;

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IAgenceRepository agenceRepository;
    private final IEquipementRepository equipementRepository;
    private final IMaintenanceRepository maintenanceRepository;
    private final VehiculeMapper vehiculeMapper;

    @Override
    public VehiculeResponseDTO create(VehiculeRequestDTO dto) {
        Vehicule vehicule = vehiculeMapper.toEntity(dto);
        appliquerAgence(vehicule, dto.getAgenceId());
        appliquerEquipements(vehicule, dto.getEquipementIds());
        return vehiculeMapper.toDTO(vehiculeRepository.save(vehicule));
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeResponseDTO findById(Long id) {
        return vehiculeMapper.toDTO(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResponseDTO> findAll() {
        return vehiculeRepository.findAll().stream()
                .map(vehiculeMapper::toDTO)
                .toList();
    }

    @Override
    public VehiculeResponseDTO update(Long id, VehiculeRequestDTO dto) {
        Vehicule vehicule = findEntity(id);
        vehiculeMapper.updateEntity(vehicule, dto);
        appliquerAgence(vehicule, dto.getAgenceId());
        appliquerEquipements(vehicule, dto.getEquipementIds());
        return vehiculeMapper.toDTO(vehiculeRepository.save(vehicule));
    }

    @Override
    public void deleteById(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicule", id);
        }
        vehiculeRepository.deleteById(id);
    }

    @Override
    public VehiculeDTO affecterVehiculeAEquipement(Long idVehicule, Long idEquipement) {
        validerIdentifiant("L'identifiant du véhicule", idVehicule);
        validerIdentifiant("L'identifiant de l'équipement", idEquipement);
        Vehicule vehicule = findEntity(idVehicule);
        Equipement equipement = equipementRepository.findById(idEquipement)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", idEquipement));
        appliquerEquipement(vehicule, equipement);
        return vehiculeMapper.toVehiculeDTO(vehiculeRepository.save(vehicule));
    }

    @Override
    public VehiculeDTO affecterMaintenanceAvehicule(String description, String immatriculation) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("La description de la maintenance est obligatoire");
        }
        if (immatriculation == null || immatriculation.isBlank()) {
            throw new IllegalArgumentException("L'immatriculation du véhicule est obligatoire");
        }
        Vehicule vehicule = vehiculeRepository.findByImmatriculation(immatriculation.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Le véhicule avec l'immatriculation " + immatriculation + " est introuvable"));
        Maintenance maintenance = new Maintenance();
        maintenance.setDateDebut(LocalDate.now());
        maintenance.setDescription(description);
        maintenance.setVehicule(vehicule);
        maintenanceRepository.save(maintenance);
        return vehiculeMapper.toVehiculeDTO(vehicule);
    }

    private void appliquerEquipement(Vehicule vehicule, Equipement equipement) {
        Set<Equipement> equipements = vehicule.getEquipements() == null
                ? new HashSet<>()
                : new HashSet<>(vehicule.getEquipements());
        equipements.add(equipement);
        vehicule.setEquipements(equipements);
    }

    private void validerIdentifiant(String nom, Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(nom + " doit être positif");
        }
    }

    private void appliquerAgence(Vehicule vehicule, Long agenceId) {
        if (agenceId == null) {
            vehicule.setAgence(null);
            return;
        }
        Agence agence = agenceRepository.findById(agenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", agenceId));
        vehicule.setAgence(agence);
    }

    private void appliquerEquipements(Vehicule vehicule, Set<Long> equipementIds) {
        if (equipementIds == null || equipementIds.isEmpty()) {
            vehicule.setEquipements(new HashSet<>());
            return;
        }
        Set<Equipement> equipements = new HashSet<>();
        for (Long equipementId : equipementIds) {
            Equipement equipement = equipementRepository.findById(equipementId)
                    .orElseThrow(() -> new ResourceNotFoundException("Equipement", equipementId));
            equipements.add(equipement);
        }
        vehicule.setEquipements(equipements);
    }

    private Vehicule findEntity(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }
}
