package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.List;

import tn.esprit.autoloc.dto.ContratDTO;
import tn.esprit.autoloc.dto.ContratRequestDTO;
import tn.esprit.autoloc.dto.ContratResponseDTO;

public interface IContratService {

    ContratResponseDTO create(ContratRequestDTO dto);

    ContratResponseDTO findById(Long id);

    List<ContratResponseDTO> findAll();

    ContratResponseDTO update(Long id, ContratRequestDTO dto);

    void deleteById(Long id);

    ContratDTO affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin);
}
