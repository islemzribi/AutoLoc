package tn.esprit.autoloc.web.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tn.esprit.autoloc.dto.ContratDTO;
import tn.esprit.autoloc.service.IContratService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContratController.class)
class ContratControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IContratService contratService;

    @Test
    void affecterContratAReservation_DelegueAuService() throws Exception {
        ContratDTO response = ContratDTO.builder()
                .idContrat(33L)
                .reservationId(5L)
                .build();
        when(contratService.affecterContratAReservation(LocalDate.of(2026, 1, 20), LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 5)))
                .thenReturn(response);

        mockMvc.perform(post("/contrats/affecter")
                        .param("dateSignature", "2026-01-20")
                        .param("dateDebut", "2026-02-01")
                        .param("dateFin", "2026-02-05"))
                .andExpect(status().isCreated());

        verify(contratService).affecterContratAReservation(LocalDate.of(2026, 1, 20), LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 5));
    }
}
