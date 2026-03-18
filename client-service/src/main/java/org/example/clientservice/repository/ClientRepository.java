package org.example.clientservice.repository;

import org.example.clientservice.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client ,Long> {

    Optional<Client> findByEmail(String email);
    List<Client> findByStatut(Client.StatutClient statutClient);
    boolean existsByEmail(String email);
}
