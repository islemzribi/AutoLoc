package tn.esprit.autoloc.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.dto.EquipementRequestDTO;
import tn.esprit.autoloc.dto.EquipementResponseDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.EquipementMapper;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.service.IEquipementService;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipementServiceImpl implements IEquipementService {

    private final IEquipementRepository equipementRepository;
    private final EquipementMapper equipementMapper;

    @Override
    public EquipementResponseDTO create(EquipementRequestDTO dto) {
        Equipement equipement = equipementMapper.toEntity(dto);
        return equipementMapper.toDTO(equipementRepository.save(equipement));
    }

    @Override
    @Transactional(readOnly = true)
    public EquipementResponseDTO findById(Long id) {
        return equipementMapper.toDTO(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipementResponseDTO> findAll() {
        return equipementRepository.findAll().stream()
                .map(equipementMapper::toDTO)
                .toList();
    }

    @Override
    public EquipementResponseDTO update(Long id, EquipementRequestDTO dto) {
        Equipement equipement = findEntity(id);
        equipementMapper.updateEntity(equipement, dto);
        return equipementMapper.toDTO(equipementRepository.save(equipement));
    }

    @Override
    public void deleteById(Long id) {
        if (!equipementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Equipement", id);
        }
        equipementRepository.deleteById(id);
    }

    private Equipement findEntity(Long id) {
        return equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", id));
    }
}
