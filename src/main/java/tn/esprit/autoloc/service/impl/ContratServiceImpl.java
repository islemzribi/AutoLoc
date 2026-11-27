package tn.esprit.autoloc.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.dto.ContratDTO;
import tn.esprit.autoloc.dto.ContratRequestDTO;
import tn.esprit.autoloc.dto.ContratResponseDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.ContratMapper;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.service.IContratService;

@Service
@RequiredArgsConstructor
@Transactional
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final IReservationRepository reservationRepository;
    private final ContratMapper contratMapper;

    @Override
    public ContratResponseDTO create(ContratRequestDTO dto) {
        Contrat contrat = contratMapper.toEntity(dto);
        appliquerReservation(contrat, dto.getReservationId());
        return contratMapper.toDTO(contratRepository.save(contrat));
    }

    @Override
    @Transactional(readOnly = true)
    public ContratResponseDTO findById(Long id) {
        return contratMapper.toDTO(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContratResponseDTO> findAll() {
        return contratRepository.findAll().stream()
                .map(contratMapper::toDTO)
                .toList();
    }

    @Override
    public ContratResponseDTO update(Long id, ContratRequestDTO dto) {
        Contrat contrat = findEntity(id);
        contratMapper.updateEntity(contrat, dto);
        appliquerReservation(contrat, dto.getReservationId());
        return contratMapper.toDTO(contratRepository.save(contrat));
    }

    @Override
    public void deleteById(Long id) {
        Contrat contrat = findEntity(id);
        if (contrat.getReservation() != null) {
            contrat.getReservation().setContrat(null);
        }
        contratRepository.delete(contrat);
    }

    /**
     * Associe un contrat à la réservation la plus récente encore sans contrat.
     * Les dates de signature, de début et de fin sont validées avant association.
     */
    @Override
    public ContratDTO affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin) {
        validerDates(dateSignature, dateDebut, dateFin);
        Reservation reservation = trouverReservationSansContrat();
        if (reservation.getContrat() != null) {
            throw new IllegalArgumentException("La réservation " + reservation.getIdReservation()
                    + " est déjà liée à un contrat");
        }
        ContratRequestDTO dto = new ContratRequestDTO(
                dateSignature, BigDecimal.ZERO, false, reservation.getIdReservation());
        Contrat contrat = contratMapper.toEntity(dto);
        contrat.setReservation(reservation);
        return contratMapper.toContratDTO(contratRepository.save(contrat));
    }

    private Reservation trouverReservationSansContrat() {
        List<Reservation> reservations = reservationRepository.findAllByContratIsNull();
        if (reservations == null || reservations.isEmpty()) {
            throw new ResourceNotFoundException("Aucune réservation sans contrat");
        }
        return reservations.stream()
                .max(Comparator.comparing(Reservation::getDateDebut,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Reservation::getIdReservation,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                .orElseThrow(() -> new ResourceNotFoundException("Aucune réservation sans contrat"));
    }

    private void validerDates(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin) {
        if (dateSignature == null) {
            throw new IllegalArgumentException("La date de signature est obligatoire");
        }
        if (dateDebut == null) {
            throw new IllegalArgumentException("La date de début de location est obligatoire");
        }
        if (dateFin == null) {
            throw new IllegalArgumentException("La date de fin de location est obligatoire");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
        if (dateSignature.isBefore(dateDebut) || dateSignature.isAfter(dateFin)) {
            throw new IllegalArgumentException(
                    "La date de signature doit être comprise entre la date de début et la date de fin");
        }
    }

    private void appliquerReservation(Contrat contrat, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", reservationId));
        contrat.setReservation(reservation);
    }

    private Contrat findEntity(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }
}
