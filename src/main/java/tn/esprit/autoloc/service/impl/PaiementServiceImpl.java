package tn.esprit.autoloc.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.dto.PaiementRequestDTO;
import tn.esprit.autoloc.dto.PaiementResponseDTO;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.PaiementMapper;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IPaiementRepository;
import tn.esprit.autoloc.service.IPaiementService;

@Service
@RequiredArgsConstructor
@Transactional
public class PaiementServiceImpl implements IPaiementService {

    private final IPaiementRepository paiementRepository;
    private final IContratRepository contratRepository;
    private final PaiementMapper paiementMapper;

    @Override
    public PaiementResponseDTO create(PaiementRequestDTO dto) {
        Paiement paiement = paiementMapper.toEntity(dto);
        appliquerContrat(paiement, dto.getContratId());
        return paiementMapper.toDTO(paiementRepository.save(paiement));
    }

    @Override
    @Transactional(readOnly = true)
    public PaiementResponseDTO findById(Long id) {
        return paiementMapper.toDTO(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponseDTO> findAll() {
        return paiementRepository.findAll().stream()
                .map(paiementMapper::toDTO)
                .toList();
    }

    @Override
    public PaiementResponseDTO update(Long id, PaiementRequestDTO dto) {
        Paiement paiement = findEntity(id);
        paiementMapper.updateEntity(paiement, dto);
        appliquerContrat(paiement, dto.getContratId());
        return paiementMapper.toDTO(paiementRepository.save(paiement));
    }

    @Override
    public void deleteById(Long id) {
        if (!paiementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paiement", id);
        }
        paiementRepository.deleteById(id);
    }

    private void appliquerContrat(Paiement paiement, Long contratId) {
        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", contratId));
        paiement.setContrat(contrat);
    }

    private Paiement findEntity(Long id) {
        return paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement", id));
    }
}
