package org.example.clientservice.service;

import org.example.clientservice.dto.ClientDTO;
import org.example.clientservice.exception.ClientNotFoundException;
import org.example.clientservice.exception.EmailAlreadyExistsException;
import org.example.clientservice.models.Client;
import org.example.clientservice.repository.ClientRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService — Unit Tests")
class ClientServiceTest {

    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientService service;

    private Client clientFixture;
    private ClientDTO dtoFixture;

    @BeforeEach
    void setUp() {
        clientFixture = new Client(
                1L, "Yanick Faical", "yanick@test.ma",
                "+212600000001", Client.StatutClient.ACTIF
        );
        dtoFixture = new ClientDTO(
                null, "Yanick Faical", "yanick@test.ma",
                "+212600000001", Client.StatutClient.ACTIF
        );
    }

    // ── findAll ──────────────────────────────────────────

    @Test
    @DisplayName("findAll — retourne la liste complète")
    void findAll_returnsAllClients() {
        when(repository.findAll()).thenReturn(List.of(clientFixture));

        List<ClientDTO> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals("Yanick Faical", result.get(0).getNom());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll — retourne liste vide")
    void findAll_returnsEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        List<ClientDTO> result = service.findAll();

        assertTrue(result.isEmpty());
    }

    // ── findById ─────────────────────────────────────────

    @Test
    @DisplayName("findById — retourne le client si trouvé")
    void findById_returnsClient_whenExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(clientFixture));

        ClientDTO result = service.findById(1L);

        assertEquals("yanick@test.ma", result.getEmail());
    }

    @Test
    @DisplayName("findById — lève ClientNotFoundException si absent")
    void findById_throwsException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> service.findById(99L));
    }

    // ── create ───────────────────────────────────────────

    @Test
    @DisplayName("create — sauvegarde et retourne le DTO")
    void create_savesAndReturnsDTO() {
        when(repository.existsByEmail(dtoFixture.getEmail())).thenReturn(false);
        when(repository.save(any(Client.class))).thenReturn(clientFixture);

        ClientDTO result = service.create(dtoFixture);

        assertEquals("Yanick Faical", result.getNom());
        verify(repository).save(any(Client.class));
    }

    @Test
    @DisplayName("create — lève EmailAlreadyExistsException si email doublon")
    void create_throwsException_whenEmailExists() {
        when(repository.existsByEmail(dtoFixture.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> service.create(dtoFixture));
        verify(repository, never()).save(any());
    }

    // ── update ───────────────────────────────────────────

    @Test
    @DisplayName("update — modifie et retourne le DTO mis à jour")
    void update_modifiesClient() {
        ClientDTO updateDTO = new ClientDTO(
                null, "Sara Benali", "sara@test.ma",
                "+212600000002", Client.StatutClient.INACTIF
        );
        Client updated = new Client(
                1L, "Sara Benali", "sara@test.ma",
                "+212600000002", Client.StatutClient.INACTIF
        );
        when(repository.findById(1L)).thenReturn(Optional.of(clientFixture));
        when(repository.save(any(Client.class))).thenReturn(updated);

        ClientDTO result = service.update(1L, updateDTO);

        assertEquals("Sara Benali", result.getNom());
        assertEquals(Client.StatutClient.INACTIF, result.getStatut());
    }

    @Test
    @DisplayName("update — lève ClientNotFoundException si id absent")
    void update_throwsException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class,
                () -> service.update(99L, dtoFixture));
    }

    // ── delete ───────────────────────────────────────────

    @Test
    @DisplayName("delete — supprime le client existant")
    void delete_removesClient_whenExists() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete — lève ClientNotFoundException si absent")
    void delete_throwsException_whenNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ClientNotFoundException.class, () -> service.delete(99L));
        verify(repository, never()).deleteById(any());
    }
}