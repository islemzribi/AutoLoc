package tn.esprit.autoloc.web.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.dto.ReservationResponseDTO;
import tn.esprit.autoloc.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.service.IReservationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IReservationService reservationService;

    @Test
    void affecterReservationAClient_DelegueAuService() throws Exception {
        ReservationResponseDTO response = ReservationResponseDTO.builder()
                .idReservation(10L)
                .vehiculeId(1L)
                .clientId(7L)
                .build();
        when(reservationService.affecterReservationAClient(10L, "Ben Ali", "Ali")).thenReturn(response);

        mockMvc.perform(put("/reservations/10/client")
                        .param("nom", "Ben Ali")
                        .param("prenom", "Ali"))
                .andExpect(status().isOk());

        verify(reservationService).affecterReservationAClient(10L, "Ben Ali", "Ali");
    }

    @Test
    void creerReservationEtContratAssocie_DelegueAuService() throws Exception {
        ReservationWithContratRequestDTO request = new ReservationWithContratRequestDTO(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 5),
                StatutReservation.CONFIRMEE,
                1L,
                2L,
                LocalDate.of(2026, 1, 20),
                new BigDecimal("700"));
        ReservationResponseDTO response = ReservationResponseDTO.builder()
                .idReservation(99L)
                .vehiculeId(1L)
                .clientId(2L)
                .build();
        when(reservationService.creerReservationEtContratAssocie(request)).thenReturn(response);

        mockMvc.perform(post("/reservations/avec-contrat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dateDebut\":\"2026-02-01\",\"dateFin\":\"2026-02-05\",\"statut\":\"CONFIRMEE\",\"vehiculeId\":1,\"clientId\":2,\"dateSignature\":\"2026-01-20\",\"montantTotal\":700}"))
                .andExpect(status().isCreated());

        verify(reservationService).creerReservationEtContratAssocie(any(ReservationWithContratRequestDTO.class));
    }
}
