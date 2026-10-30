package tn.esprit.autoloc.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.MaintenanceRequestDTO;
import tn.esprit.autoloc.dto.MaintenanceResponseDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.MaintenanceMapper;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.service.IMaintenanceService;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceServiceImpl implements IMaintenanceService {

    private final IMaintenanceRepository maintenanceRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final MaintenanceMapper maintenanceMapper;

    @Override
    public MaintenanceResponseDTO create(MaintenanceRequestDTO dto) {
        validerDates(dto);
        Maintenance maintenance = maintenanceMapper.toEntity(dto);
        appliquerVehicule(maintenance, dto.getVehiculeId());
        return maintenanceMapper.toDTO(maintenanceRepository.save(maintenance));
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceResponseDTO findById(Long id) {
        return maintenanceMapper.toDTO(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceResponseDTO> findAll() {
        return maintenanceRepository.findAll().stream()
                .map(maintenanceMapper::toDTO)
                .toList();
    }

    @Override
    public MaintenanceResponseDTO update(Long id, MaintenanceRequestDTO dto) {
        validerDates(dto);
        Maintenance maintenance = findEntity(id);
        maintenanceMapper.updateEntity(maintenance, dto);
        appliquerVehicule(maintenance, dto.getVehiculeId());
        return maintenanceMapper.toDTO(maintenanceRepository.save(maintenance));
    }

    @Override
    public void deleteById(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Maintenance", id);
        }
        maintenanceRepository.deleteById(id);
    }

    private void appliquerVehicule(Maintenance maintenance, Long vehiculeId) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", vehiculeId));
        maintenance.setVehicule(vehicule);
    }

    private void validerDates(MaintenanceRequestDTO dto) {
        if (dto.getDateDebut() != null && dto.getDateFin() != null
                && dto.getDateFin().isBefore(dto.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }

    private Maintenance findEntity(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
    }
}
