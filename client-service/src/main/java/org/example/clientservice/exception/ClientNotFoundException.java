package org.example.clientservice.exception;

public class ClientNotFoundException extends RuntimeException{
    public ClientNotFoundException(Long id) {
        super("Client introuvable : id=" + id);
    }
}
