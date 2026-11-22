package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.List;

import tn.esprit.autoloc.dto.ContratRequestDTO;
import tn.esprit.autoloc.dto.ContratDTO;

public interface IContratService {

    ContratDTO create(ContratRequestDTO dto);

    ContratDTO findById(Long id);

    List<ContratDTO> findAll();

    ContratDTO update(Long id, ContratRequestDTO dto);

    void deleteById(Long id);

    ContratDTO affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin);
}
