package tn.esprit.autoloc.service;

import java.util.List;

import tn.esprit.autoloc.dto.PaiementRequestDTO;
import tn.esprit.autoloc.dto.PaiementResponseDTO;

public interface IPaiementService {

    PaiementResponseDTO create(PaiementRequestDTO dto);

    PaiementResponseDTO findById(Long id);

    List<PaiementResponseDTO> findAll();

    PaiementResponseDTO update(Long id, PaiementRequestDTO dto);

    void deleteById(Long id);
}
