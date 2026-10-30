package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;

import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.dto.MaintenanceRequestDTO;
import tn.esprit.autoloc.dto.MaintenanceResponseDTO;

/**
 * Conversion entre l'entité Maintenance et ses DTO.
 * Le véhicule est géré par le service, seul son id est exposé.
 */
@Component
public class MaintenanceMapper {

    public Maintenance toEntity(MaintenanceRequestDTO dto) {
        Maintenance maintenance = new Maintenance();
        updateEntity(maintenance, dto);
        return maintenance;
    }

    public void updateEntity(Maintenance maintenance, MaintenanceRequestDTO dto) {
        maintenance.setDateDebut(dto.getDateDebut());
        maintenance.setDateFin(dto.getDateFin());
        maintenance.setDescription(dto.getDescription());
    }

    public MaintenanceResponseDTO toDTO(Maintenance maintenance) {
        return MaintenanceResponseDTO.builder()
                .idMaintenance(maintenance.getIdMaintenance())
                .dateDebut(maintenance.getDateDebut())
                .dateFin(maintenance.getDateFin())
                .description(maintenance.getDescription())
                .vehiculeId(maintenance.getVehicule() != null ? maintenance.getVehicule().getIdVehicule() : null)
                .build();
    }
}
