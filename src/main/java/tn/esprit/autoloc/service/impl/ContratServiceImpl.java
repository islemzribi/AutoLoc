package tn.esprit.autoloc.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
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
