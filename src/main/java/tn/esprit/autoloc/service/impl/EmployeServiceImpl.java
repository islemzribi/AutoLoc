package tn.esprit.autoloc.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.repository.IEmployeRepository;
import tn.esprit.autoloc.service.IEmployeService;
import tn.esprit.autoloc.service.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeServiceImpl implements IEmployeService {

    private static final String NOT_FOUND = "Employe introuvable : ";

    private final IEmployeRepository employeRepository;

    @Override
    public Employe add(Employe employe) {
        return employeRepository.save(employe);
    }

    @Override
    public Employe update(Employe employe) {
        if (employe.getIdEmploye() == null || !employeRepository.existsById(employe.getIdEmploye())) {
            throw new ResourceNotFoundException(NOT_FOUND + employe.getIdEmploye());
        }
        return employeRepository.save(employe);
    }

    @Override
    @Transactional(readOnly = true)
    public Employe getById(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employe> getAll() {
        return employeRepository.findAll();
    }

    @Override
    public void delete(Employe employe) {
        employeRepository.delete(employe);
    }

    @Override
    public void deleteById(Long id) {
        if (!employeRepository.existsById(id)) {
            throw new ResourceNotFoundException(NOT_FOUND + id);
        }
        employeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return employeRepository.existsById(id);
    }
}
