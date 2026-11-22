package tn.esprit.autoloc.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.dto.ContratRequestDTO;
import tn.esprit.autoloc.dto.ContratDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.ContratMapper;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContratServiceImplTest {

    @Mock
    private IContratRepository contratRepository;

    @Mock
    private IReservationRepository reservationRepository;

    @Mock
    private ContratMapper contratMapper;

    @InjectMocks
    private ContratServiceImpl contratService;

    @Test
    void affecterContratAReservation_CreeLeContrat() {
        Reservation reservation = new Reservation();
        reservation.setIdReservation(5L);
        reservation.setDateDebut(LocalDate.of(2026, 2, 1));
        reservation.setDateFin(LocalDate.of(2026, 2, 5));
        reservation.setStatut(StatutReservation.CONFIRMEE);

        when(reservationRepository.findAllByContratIsNull()).thenReturn(List.of(reservation));
        when(contratMapper.toEntity(any(ContratRequestDTO.class))).thenAnswer(invocation -> {
            ContratRequestDTO dto = invocation.getArgument(0);
            Contrat contrat = new Contrat();
            contrat.setIdContrat(33L);
            contrat.setDateSignature(dto.getDateSignature());
            contrat.setMontantTotal(dto.getMontantTotal());
            contrat.setValide(dto.isValide());
            contrat.setReservation(reservation);
            return contrat;
        });
        when(contratRepository.save(any(Contrat.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(contratMapper.toDTO(any(Contrat.class))).thenReturn(ContratDTO.builder()
                .idContrat(33L)
                .reservationId(5L)
                .build());

        ContratDTO result = contratService.affecterContratAReservation(LocalDate.of(2026, 2, 2), LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 5));

        assertNotNull(result);
        assertEquals(33L, result.getIdContrat());
        assertEquals(5L, result.getReservationId());
        verify(contratRepository).save(any(Contrat.class));
    }

    @Test
    void affecterContratAReservation_DatesInvalides_ThrowException() {
        assertThrows(IllegalArgumentException.class, () -> contratService.affecterContratAReservation(
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 5)));
    }

    @Test
    void affecterContratAReservation_ReservationDejaContra_ThrowException() {
        Reservation reservation = new Reservation();
        reservation.setIdReservation(5L);
        reservation.setContrat(new Contrat());
        when(reservationRepository.findAllByContratIsNull()).thenReturn(List.of(reservation));

        assertThrows(IllegalArgumentException.class, () -> contratService.affecterContratAReservation(
                LocalDate.of(2026, 2, 2), LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 5)));
    }

    @Test
    void affecterContratAReservation_AucuneReservation_ThrowException() {
        when(reservationRepository.findAllByContratIsNull()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> contratService.affecterContratAReservation(
                LocalDate.of(2026, 2, 2), LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 5)));
    }
}