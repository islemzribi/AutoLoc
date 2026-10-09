package tn.esprit.autoloc.service;

import java.util.List;

import tn.esprit.autoloc.domain.Client;

public interface IClientService {

    Client add(Client client);

    Client update(Client client);

    Client getById(Long id);

    List<Client> getAll();

    List<Client> getAllSortedByNom();

    void delete(Client client);

    void deleteById(Long id);

    boolean existsById(Long id);
}
