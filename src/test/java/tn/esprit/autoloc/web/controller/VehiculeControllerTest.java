package tn.esprit.autoloc.web.controller;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tn.esprit.autoloc.dto.VehiculeDTO;
import tn.esprit.autoloc.service.IVehiculeService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehiculeController.class)
class VehiculeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IVehiculeService vehiculeService;

    @Test
    void affecterVehiculeAEquipement_DelegueAuService() throws Exception {
        VehiculeDTO response = VehiculeDTO.builder()
                .idVehicule(1L)
                .immatriculation("TN-2026-AUTOLOC")
                .equipementIds(Set.of(2L))
                .build();
        when(vehiculeService.affecterVehiculeAEquipement(1L, 2L)).thenReturn(response);

        mockMvc.perform(put("/vehicules/1/equipements/2"))
                .andExpect(status().isOk());

        verify(vehiculeService).affecterVehiculeAEquipement(1L, 2L);
    }

    @Test
    void affecterMaintenanceAvehicule_DelegueAuService() throws Exception {
        VehiculeDTO response = VehiculeDTO.builder()
                .idVehicule(1L)
                .immatriculation("TN-2026-AUTOLOC")
                .build();
        when(vehiculeService.affecterMaintenanceAvehicule("Lavage complet", "TN-2026-AUTOLOC")).thenReturn(response);

        mockMvc.perform(post("/vehicules/maintenance")
                        .param("immatriculation", "TN-2026-AUTOLOC")
                        .param("description", "Lavage complet"))
                .andExpect(status().isCreated());

        verify(vehiculeService).affecterMaintenanceAvehicule("Lavage complet", "TN-2026-AUTOLOC");
    }
}
