package tn.esprit.autoloc.service;

import java.util.List;

import tn.esprit.autoloc.dto.VehiculeDTO;
import tn.esprit.autoloc.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.dto.VehiculeResponseDTO;

public interface IVehiculeService {

    VehiculeResponseDTO create(VehiculeRequestDTO dto);

    VehiculeResponseDTO findById(Long id);

    List<VehiculeResponseDTO> findAll();

    VehiculeResponseDTO update(Long id, VehiculeRequestDTO dto);

    void deleteById(Long id);

    VehiculeDTO affecterVehiculeAEquipement(Long idVehicule, Long idEquipement);

    VehiculeDTO affecterMaintenanceAvehicule(String description, String immatriculation);
}
