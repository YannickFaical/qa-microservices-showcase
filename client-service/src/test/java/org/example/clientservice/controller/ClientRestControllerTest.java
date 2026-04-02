package org.example.clientservice.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.example.clientservice.models.Client;
import org.example.clientservice.repository.ClientRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ClientRestController — API Tests")
class ClientRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ClientRepository repository;

    @BeforeEach
    void setUp() {
        System.out.println(">>> Démarrage du test sur le port : " + port);
        RestAssured.port = port;
        // Correction : pas de slash à la fin du basePath
        RestAssured.basePath = "/api/clients";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        repository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("GET / — liste vide au départ")
    void getAll_returnsEmptyList() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get() // Appelle /api/clients
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    @Order(2)
    @DisplayName("POST / — crée un client et retourne 201")
    void create_returns201() {
        String body = """
            {
                "nom": "Yanick Faical",
                "email": "yanick@test.ma",
                "telephone": "+212600000001",
                "statut": "ACTIF"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nom", equalTo("Yanick Faical"))
                .body("email", equalTo("yanick@test.ma"))
                .body("statut", equalTo("ACTIF"));
    }

    @Test
    @Order(3)
    @DisplayName("POST / — email doublon retourne 409")
    void create_duplicateEmail_returns409() {
        Client existing = new Client();
        existing.setNom("Existing");
        existing.setEmail("yanick@test.ma");
        existing.setTelephone("+212600000001");
        existing.setStatut(Client.StatutClient.ACTIF);
        repository.save(existing);

        String body = """
            {
                "nom": "Autre",
                "email": "yanick@test.ma",
                "telephone": "+212600000002",
                "statut": "ACTIF"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post()
                .then()
                .statusCode(409)
                .body("error", containsString("yanick@test.ma"));
    }

    @Test
    @Order(4)
    @DisplayName("POST / — champs vides retourne 400")
    void create_emptyFields_returns400() {
        String body = """
            {
                "nom": "",
                "email": "invalid-email",
                "telephone": ""
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    @Order(5)
    @DisplayName("GET /{id} — retourne le client")
    void getById_returnsClient() {
        Client c = new Client();
        c.setNom("Sara Benali");
        c.setEmail("sara@test.ma");
        c.setTelephone("+212600000002");
        c.setStatut(Client.StatutClient.ACTIF);
        Long id = repository.save(c).getId();

        given()
                .pathParam("id", id)
                .when()
                .get("/{id}") // Utilisation propre des paramètres
                .then()
                .statusCode(200)
                .body("nom", equalTo("Sara Benali"));
    }

    @Test
    @Order(6)
    @DisplayName("GET /{id} — id inexistant retourne 404")
    void getById_notFound_returns404() {
        given()
                .pathParam("id", 9999)
                .when()
                .get("/{id}")
                .then()
                .statusCode(404)
                .body("error", containsString("9999"));
    }

    @Test
    @Order(7)
    @DisplayName("PUT /{id} — met à jour le client")
    void update_modifiesClient() {
        Client c = new Client();
        c.setNom("Old Name");
        c.setEmail("old@test.ma");
        c.setTelephone("+212600000003");
        c.setStatut(Client.StatutClient.ACTIF);
        Long id = repository.save(c).getId();

        String body = """
            {
                "nom": "New Name",
                "email": "new@test.ma",
                "telephone": "+212600000003",
                "statut": "INACTIF"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .body(body)
                .when()
                .put("/{id}")
                .then()
                .statusCode(200)
                .body("nom", equalTo("New Name"))
                .body("statut", equalTo("INACTIF"));
    }

    @Test
    @Order(8)
    @DisplayName("DELETE /{id} — supprime le client")
    void delete_removesClient() {
        Client c = new Client();
        c.setNom("To Delete");
        c.setEmail("delete@test.ma");
        c.setTelephone("+212600000004");
        c.setStatut(Client.StatutClient.ACTIF);
        Long id = repository.save(c).getId();

        // 1. Suppression
        given()
                .pathParam("id", id)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(204);

        // 2. Vérification
        given()
                .pathParam("id", id)
                .when()
                .get("/{id}")
                .then()
                .statusCode(404);
    }
}