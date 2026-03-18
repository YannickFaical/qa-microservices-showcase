package org.example.clientservice.service;

import lombok.RequiredArgsConstructor;
import org.example.clientservice.dto.ClientDTO;
import org.example.clientservice.exception.ClientNotFoundException;
import org.example.clientservice.exception.EmailAlreadyExistsException;
import org.example.clientservice.models.Client;
import org.example.clientservice.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class ClientService {

    private final ClientRepository  repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }


public List<ClientDTO> findAll(){

    return  repository.findAll().stream()
            .map(ClientDTO::fromEntity)
            .toList();
}

public ClientDTO findById(Long id){
    return repository.findById(id)
            .map(ClientDTO::fromEntity)
            .orElseThrow(() -> new ClientNotFoundException(id));
}
    public ClientDTO create(ClientDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }
        Client saved = repository.save(dto.toEntity());
        return ClientDTO.fromEntity(saved);
    }

    public ClientDTO update(Long id, ClientDTO dto) {
        Client existing = repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        existing.setNom(dto.getNom());
        existing.setEmail(dto.getEmail());
        existing.setTelephone(dto.getTelephone());
        if (dto.getStatut() != null) existing.setStatut(dto.getStatut());
        return ClientDTO.fromEntity(repository.save(existing));
    }
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ClientNotFoundException(id);
        repository.deleteById(id);
    }


}
