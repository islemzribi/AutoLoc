package tn.esprit.autoloc.service;

import java.util.List;

import tn.esprit.autoloc.dto.EquipementRequestDTO;
import tn.esprit.autoloc.dto.EquipementResponseDTO;

public interface IEquipementService {

    EquipementResponseDTO create(EquipementRequestDTO dto);

    EquipementResponseDTO findById(Long id);

    List<EquipementResponseDTO> findAll();

    EquipementResponseDTO update(Long id, EquipementRequestDTO dto);

    void deleteById(Long id);
}
