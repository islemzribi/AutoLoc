package tn.esprit.autoloc.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.dto.EmployeRequestDTO;
import tn.esprit.autoloc.dto.EmployeResponseDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.EmployeMapper;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEmployeRepository;
import tn.esprit.autoloc.service.IEmployeService;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeServiceImpl implements IEmployeService {

    private final IEmployeRepository employeRepository;
    private final IAgenceRepository agenceRepository;
    private final EmployeMapper employeMapper;

    @Override
    public EmployeResponseDTO create(EmployeRequestDTO dto) {
        Employe employe = employeMapper.toEntity(dto);
        appliquerAgence(employe, dto.getAgenceId());
        return employeMapper.toDTO(employeRepository.save(employe));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeResponseDTO findById(Long id) {
        return employeMapper.toDTO(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeResponseDTO> findAll() {
        return employeRepository.findAll().stream()
                .map(employeMapper::toDTO)
                .toList();
    }

    @Override
    public EmployeResponseDTO update(Long id, EmployeRequestDTO dto) {
        Employe employe = findEntity(id);
        employeMapper.updateEntity(employe, dto);
        appliquerAgence(employe, dto.getAgenceId());
        return employeMapper.toDTO(employeRepository.save(employe));
    }

    @Override
    public void deleteById(Long id) {
        if (!employeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employe", id);
        }
        employeRepository.deleteById(id);
    }

    private void appliquerAgence(Employe employe, Long agenceId) {
        if (agenceId == null) {
            employe.setAgence(null);
            return;
        }
        Agence agence = agenceRepository.findById(agenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", agenceId));
        employe.setAgence(agence);
    }

    private Employe findEntity(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
    }
}
