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
import tn.esprit.autoloc.dto.AgenceRequestDTO;
import tn.esprit.autoloc.dto.AgenceResponseDTO;
import tn.esprit.autoloc.service.IAgenceService;

/**
 * Endpoints REST de gestion des agences (uniquement des DTO en entrée/sortie).
 */
@RestController
@RequestMapping("/agences")
@RequiredArgsConstructor
public class AgenceController {

    private final IAgenceService agenceService;

    @GetMapping
    public List<AgenceResponseDTO> findAll() {
        return agenceService.findAll();
    }

    @GetMapping("/{id}")
    public AgenceResponseDTO findById(@PathVariable Long id) {
        return agenceService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgenceResponseDTO create(@Valid @RequestBody AgenceRequestDTO dto) {
        return agenceService.create(dto);
    }

    @PutMapping("/{id}")
    public AgenceResponseDTO update(@PathVariable Long id, @Valid @RequestBody AgenceRequestDTO dto) {
        return agenceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        agenceService.deleteById(id);
    }
}
