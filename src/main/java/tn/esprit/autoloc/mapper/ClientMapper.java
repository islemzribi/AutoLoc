package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;

import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.dto.ClientRequestDTO;
import tn.esprit.autoloc.dto.ClientResponseDTO;

/**
 * Conversion entre l'entité Client et ses DTO.
 */
@Component
public class ClientMapper {

    public Client toEntity(ClientRequestDTO dto) {
        Client client = new Client();
        updateEntity(client, dto);
        return client;
    }

    public void updateEntity(Client client, ClientRequestDTO dto) {
        client.setNom(dto.getNom());
        client.setPrenom(dto.getPrenom());
        client.setEmail(dto.getEmail());
        client.setTelephone(dto.getTelephone());
        client.setNumPermis(dto.getNumPermis());
        client.setDateInscription(dto.getDateInscription());
    }

    public ClientResponseDTO toDTO(Client client) {
        return ClientResponseDTO.builder()
                .idClient(client.getIdClient())
                .nom(client.getNom())
                .prenom(client.getPrenom())
                .email(client.getEmail())
                .telephone(client.getTelephone())
                .numPermis(client.getNumPermis())
                .dateInscription(client.getDateInscription())
                .build();
    }
}
