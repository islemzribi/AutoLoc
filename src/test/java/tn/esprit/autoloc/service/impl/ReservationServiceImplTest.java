package tn.esprit.autoloc.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.dto.ReservationRequestDTO;
import tn.esprit.autoloc.dto.ReservationResponseDTO;
import tn.esprit.autoloc.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.ReservationMapper;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private IReservationRepository reservationRepository;

    @Mock
    private IVehiculeRepository vehiculeRepository;

    @Mock
    private IClientRepository clientRepository;

    @Mock
    private IContratRepository contratRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    void affecterReservationAClient_RetourneLaReservationAvecLeClient() {
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(1L);
        Client client = new Client();
        client.setIdClient(7L);
        client.setNom("Ben Ali");
        client.setPrenom("Ali");
        Reservation reservation = reservation(10L, vehicule, null);

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));
        when(clientRepository.findByNomAndPrenom("Ben Ali", "Ali")).thenReturn(List.of(client));
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        when(reservationMapper.toDTO(reservation)).thenReturn(ReservationResponseDTO.builder()
                .idReservation(10L)
                .vehiculeId(1L)
                .clientId(7L)
                .build());

        ReservationResponseDTO result = reservationService.affecterReservationAClient(10L, "Ben Ali", "Ali");

        assertNotNull(result);
        assertEquals(10L, result.getIdReservation());
        assertEquals(7L, result.getClientId());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void affecterReservationAClient_ReservationInexistante_ThrowException() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reservationService.affecterReservationAClient(999L, "Ben Ali", "Ali"));
    }

    @Test
    void affecterReservationAClient_ClientInexistant_ThrowException() {
        Reservation reservation = reservation(10L, new Vehicule(), null);
        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));
        when(clientRepository.findByNomAndPrenom("Ben Ali", "Ali")).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> reservationService.affecterReservationAClient(10L, "Ben Ali", "Ali"));
    }

    @Test
    void affecterReservationAClient_PlusieursClients_ThrowException() {
        Reservation reservation = reservation(10L, new Vehicule(), null);
        Client client = new Client();
        client.setIdClient(7L);
        client.setNom("Ben Ali");
        client.setPrenom("Ali");
        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));
        when(clientRepository.findByNomAndPrenom("Ben Ali", "Ali")).thenReturn(List.of(client, client));

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.affecterReservationAClient(10L, "Ben Ali", "Ali"));
    }

    @Test
    void affecterReservationAClient_NomClientAbsent_ThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> reservationService.affecterReservationAClient(10L, null, "Ali"));
    }

    @Test
    void creerReservationEtContratAssocie_CreeReservationEtContrat() {
        ReservationWithContratRequestDTO requestDTO = new ReservationWithContratRequestDTO(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 5),
                StatutReservation.CONFIRMEE,
                1L,
                2L,
                LocalDate.of(2026, 1, 20),
                new BigDecimal("700"));

        when(reservationMapper.toEntity(any(ReservationRequestDTO.class))).thenAnswer(invocation -> {
            ReservationRequestDTO dto = invocation.getArgument(0);
            Reservation reservation = new Reservation();
            reservation.setIdReservation(99L);
            reservation.setDateDebut(dto.getDateDebut());
            reservation.setDateFin(dto.getDateFin());
            reservation.setStatut(dto.getStatut());
            reservation.setVehicule(vehicule(1L));
            reservation.setClient(client(2L));
            return reservation;
        });
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule(1L)));
        when(clientRepository.findById(2L)).thenReturn(Optional.of(client(2L)));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(contratRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(ReservationResponseDTO.builder()
                .idReservation(99L)
                .vehiculeId(1L)
                .clientId(2L)
                .build());

        ReservationResponseDTO result = reservationService.creerReservationEtContratAssocie(requestDTO);

        assertNotNull(result);
        assertEquals(99L, result.getIdReservation());
        verify(contratRepository).save(any());
    }

    @Test
    void creerReservationEtContratAssocie_VehiculeInexistant_ThrowException() {
        ReservationWithContratRequestDTO requestDTO = new ReservationWithContratRequestDTO(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 5),
                StatutReservation.CONFIRMEE,
                1L,
                2L,
                LocalDate.of(2026, 1, 20),
                new BigDecimal("700"));
        when(reservationMapper.toEntity(any(ReservationRequestDTO.class))).thenAnswer(invocation -> {
            ReservationRequestDTO dto = invocation.getArgument(0);
            Reservation reservation = new Reservation();
            reservation.setIdReservation(99L);
            reservation.setDateDebut(dto.getDateDebut());
            reservation.setDateFin(dto.getDateFin());
            reservation.setStatut(dto.getStatut());
            reservation.setVehicule(vehicule(1L));
            reservation.setClient(client(2L));
            return reservation;
        });
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reservationService.creerReservationEtContratAssocie(requestDTO));
    }

    private Reservation reservation(Long idReservation, Vehicule vehicule, Client client) {
        Reservation reservation = new Reservation();
        reservation.setIdReservation(idReservation);
        reservation.setDateDebut(LocalDate.of(2026, 2, 1));
        reservation.setDateFin(LocalDate.of(2026, 2, 5));
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setVehicule(vehicule);
        reservation.setClient(client);
        return reservation;
    }

    private Vehicule vehicule(Long idVehicule) {
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(idVehicule);
        return vehicule;
    }

    private Client client(Long idClient) {
        Client client = new Client();
        client.setIdClient(idClient);
        return client;
    }
}
