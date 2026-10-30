package tn.esprit.autoloc.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.dto.VehiculeResponseDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.VehiculeMapper;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.service.IVehiculeService;

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IAgenceRepository agenceRepository;
    private final IEquipementRepository equipementRepository;
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
