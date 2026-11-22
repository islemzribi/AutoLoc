package tn.esprit.autoloc.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.VehiculeDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.VehiculeMapper;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehiculeServiceImplTest {

    @Mock
    private IVehiculeRepository vehiculeRepository;

    @Mock
    private IAgenceRepository agenceRepository;

    @Mock
    private IEquipementRepository equipementRepository;

    @Mock
    private IMaintenanceRepository maintenanceRepository;

    @Mock
    private VehiculeMapper vehiculeMapper;

    @InjectMocks
    private VehiculeServiceImpl vehiculeService;

    @Test
    void affecterVehiculeAEquipement_AjouteLequipement() {
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(1L);
        vehicule.setImmatriculation("TN-2026-AUTOLOC");
        vehicule.setMarque("Renault");
        vehicule.setModele("Clio");
        vehicule.setEquipements(new HashSet<>());
        Equipement equipement = new Equipement();
        equipement.setIdEquipement(2L);
        equipement.setLibelle("GPS");

        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));
        when(equipementRepository.findById(2L)).thenReturn(Optional.of(equipement));
        when(vehiculeRepository.save(vehicule)).thenReturn(vehicule);
        when(vehiculeMapper.toDTO(vehicule)).thenReturn(VehiculeDTO.builder()
                .idVehicule(1L)
                .immatriculation("TN-2026-AUTOLOC")
                .equipementIds(Set.of(2L))
                .build());

        VehiculeDTO result = vehiculeService.affecterVehiculeAEquipement(1L, 2L);

        assertNotNull(result);
        assertEquals(1L, result.getIdVehicule());
        assertTrue(result.getEquipementIds().contains(2L));
        verify(vehiculeRepository).save(vehicule);
    }

    @Test
    void affecterVehiculeAEquipement_VehiculeInexistant_ThrowException() {
        when(vehiculeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vehiculeService.affecterVehiculeAEquipement(999L, 2L));
    }

    @Test
    void affecterVehiculeAEquipement_EquipementInexistant_ThrowException() {
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(1L);
        vehicule.setEquipements(new HashSet<>());
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));
        when(equipementRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vehiculeService.affecterVehiculeAEquipement(1L, 999L));
    }

    @Test
    void affecterVehiculeAEquipement_DejaAffecte_DoublonEvite() {
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(1L);
        Equipement equipement = new Equipement();
        equipement.setIdEquipement(2L);
        Set<Equipement> equipements = new HashSet<>();
        equipements.add(equipement);
        vehicule.setEquipements(equipements);

        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));
        when(equipementRepository.findById(2L)).thenReturn(Optional.of(equipement));
        when(vehiculeRepository.save(vehicule)).thenReturn(vehicule);
        when(vehiculeMapper.toDTO(vehicule)).thenReturn(VehiculeDTO.builder()
                .idVehicule(1L)
                .equipementIds(Set.of(2L))
                .build());

        VehiculeDTO result = vehiculeService.affecterVehiculeAEquipement(1L, 2L);

        assertNotNull(result);
        assertEquals(1, vehicule.getEquipements().size());
    }

    @Test
    void affecterMaintenanceAvehicule_RetourneLeVehiculeMisAJour() {
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(1L);
        vehicule.setImmatriculation("TN-2026-AUTOLOC");
        vehicule.setMarque("Renault");
        vehicule.setModele("Clio");

        when(vehiculeRepository.findByImmatriculation("TN-2026-AUTOLOC")).thenReturn(Optional.of(vehicule));
        when(maintenanceRepository.save(any(Maintenance.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(vehiculeMapper.toDTO(vehicule)).thenReturn(VehiculeDTO.builder()
                .idVehicule(1L)
                .immatriculation("TN-2026-AUTOLOC")
                .marque("Renault")
                .modele("Clio")
                .build());

        VehiculeDTO result = vehiculeService.affecterMaintenanceAvehicule("Lavage complet", "TN-2026-AUTOLOC");

        assertNotNull(result);
        assertEquals(1L, result.getIdVehicule());
        verify(maintenanceRepository).save(any(Maintenance.class));
    }

    @Test
    void affecterMaintenanceAvehicule_DescriptionVide_ThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> vehiculeService.affecterMaintenanceAvehicule("", "TN-2026-AUTOLOC"));
    }

    @Test
    void affecterMaintenanceAvehicule_ImmatriculationVide_ThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> vehiculeService.affecterMaintenanceAvehicule("Lavage complet", " "));
    }

    @Test
    void affecterMaintenanceAvehicule_VehiculeInexistant_ThrowException() {
        when(vehiculeRepository.findByImmatriculation("TN-INCONNU")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vehiculeService.affecterMaintenanceAvehicule("Lavage complet", "TN-INCONNU"));
    }
}

