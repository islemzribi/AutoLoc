package tn.esprit.autoloc.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class AgenceServiceImpl implements IAgenceService {

    private static final String NOT_FOUND = "Agence introuvable : ";

    private final IAgenceRepository agenceRepository;

    @Override
    public Agence add(Agence agence) {
        return agenceRepository.save(agence);
    }

    @Override
    public Agence update(Agence agence) {
        if (agence.getIdAgence() == null || !agenceRepository.existsById(agence.getIdAgence())) {
            throw new ResourceNotFoundException(NOT_FOUND + agence.getIdAgence());
        }
        return agenceRepository.save(agence);
    }

    @Override
    @Transactional(readOnly = true)
    public Agence getById(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Agence> getAll() {
        return agenceRepository.findAll();
    }

    @Override
    public void delete(Agence agence) {
        agenceRepository.delete(agence);
    }

    @Override
    public void deleteById(Long id) {
        if (!agenceRepository.existsById(id)) {
            throw new ResourceNotFoundException(NOT_FOUND + id);
        }
        agenceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return agenceRepository.existsById(id);
    }
}
