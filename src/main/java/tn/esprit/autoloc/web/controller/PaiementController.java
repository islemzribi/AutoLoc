package tn.esprit.autoloc.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.autoloc.dto.PaiementRequestDTO;
import tn.esprit.autoloc.dto.PaiementResponseDTO;
import tn.esprit.autoloc.service.IPaiementService;

/**
 * Endpoints REST de gestion des paiements (uniquement des DTO en entrée/sortie).
 */
@RestController
@RequestMapping("/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final IPaiementService paiementService;

    @GetMapping
    public List<PaiementResponseDTO> findAll() {
        return paiementService.findAll();
    }

    @GetMapping("/{id}")
    public PaiementResponseDTO findById(@PathVariable Long id) {
        return paiementService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaiementResponseDTO create(@Valid @RequestBody PaiementRequestDTO dto) {
        return paiementService.create(dto);
    }

    @PutMapping("/{id}")
    public PaiementResponseDTO update(@PathVariable Long id, @Valid @RequestBody PaiementRequestDTO dto) {
        return paiementService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        paiementService.deleteById(id);
    }
}
