package tn.esprit.autoloc.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.ReservationRequestDTO;
import tn.esprit.autoloc.dto.ReservationResponseDTO;
import tn.esprit.autoloc.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.ReservationMapper;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.service.IReservationService;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IClientRepository clientRepository;
    private final IContratRepository contratRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public ReservationResponseDTO create(ReservationRequestDTO dto) {
        validerDates(dto);
        Reservation reservation = reservationMapper.toEntity(dto);
        appliquerRelations(reservation, dto);
        return reservationMapper.toDTO(reservationRepository.save(reservation));
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponseDTO findById(Long id) {
        return reservationMapper.toDTO(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> findAll() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDTO)
                .toList();
    }

    @Override
    public ReservationResponseDTO update(Long id, ReservationRequestDTO dto) {
        validerDates(dto);
        Reservation reservation = findEntity(id);
        reservationMapper.updateEntity(reservation, dto);
        appliquerRelations(reservation, dto);
        return reservationMapper.toDTO(reservationRepository.save(reservation));
    }

    @Override
    public void deleteById(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation", id);
        }
        reservationRepository.deleteById(id);
    }

    @Override
    public ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom) {
        validerTexte("nom", nom);
        validerTexte("prenom", prenom);
        Reservation reservation = findEntity(idReservation);
        Client client = trouverClient(nom, prenom);
        reservation.setClient(client);
        return reservationMapper.toDTO(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponseDTO creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO) {
        Reservation reservation = construireReservation(requestDTO);
        Reservation savedReservation = reservationRepository.save(reservation);
        creerContratAssocie(requestDTO, savedReservation);
        return reservationMapper.toDTO(savedReservation);
    }

    private Reservation construireReservation(ReservationWithContratRequestDTO requestDTO) {
        ReservationRequestDTO dto = new ReservationRequestDTO(
                requestDTO.getDateDebut(),
                requestDTO.getDateFin(),
                requestDTO.getStatut(),
                requestDTO.getVehiculeId(),
                requestDTO.getClientId());
        validerDates(dto);
        Reservation reservation = reservationMapper.toEntity(dto);
        appliquerRelations(reservation, dto);
        return reservation;
    }

    private void creerContratAssocie(ReservationWithContratRequestDTO requestDTO, Reservation reservation) {
        Contrat contrat = new Contrat();
        contrat.setDateSignature(requestDTO.getDateSignature());
        contrat.setMontantTotal(requestDTO.getMontantTotal());
        contrat.setValide(false);
        contrat.setReservation(reservation);
        contratRepository.save(contrat);
    }

    private Client trouverClient(String nom, String prenom) {
        List<Client> clients = clientRepository.findByNomAndPrenom(nom, prenom);
        if (clients == null || clients.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Aucun client trouvé avec le nom " + nom + " et le prénom " + prenom);
        }
        if (clients.size() > 1) {
            throw new IllegalArgumentException(
                    "Plusieurs clients correspondent au nom " + nom + " et au prénom " + prenom);
        }
        return clients.get(0);
    }

    private void validerTexte(String nom, String valeur) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le " + nom + " est obligatoire");
        }
    }

    private void appliquerRelations(Reservation reservation, ReservationRequestDTO dto) {
        Vehicule vehicule = vehiculeRepository.findById(dto.getVehiculeId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", dto.getVehiculeId()));
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", dto.getClientId()));
        reservation.setVehicule(vehicule);
        reservation.setClient(client);
    }

    private void validerDates(ReservationRequestDTO dto) {
        if (dto.getDateDebut() != null && dto.getDateFin() != null
                && dto.getDateFin().isBefore(dto.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }

    private Reservation findEntity(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }
}
