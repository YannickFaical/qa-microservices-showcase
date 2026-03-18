package org.example.clientservice.controller;

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
        RestAssured.port = port;
        RestAssured.basePath = "/api/clients";
        repository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("GET / — liste vide au départ")
    void getAll_returnsEmptyList() {
        given().contentType(ContentType.JSON)
                .when().get()
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

        given().contentType(ContentType.JSON).body(body)
                .when().post()
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

        given().contentType(ContentType.JSON).body(body)
                .when().post()
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

        given().contentType(ContentType.JSON).body(body)
                .when().post()
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
                .when().get("/" + id)
                .then()
                .statusCode(200)
                .body("nom", equalTo("Sara Benali"));
    }

    @Test
    @Order(6)
    @DisplayName("GET /{id} — id inexistant retourne 404")
    void getById_notFound_returns404() {
        given()
                .when().get("/9999")
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

        given().contentType(ContentType.JSON).body(body)
                .when().put("/" + id)
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

        given()
                .when().delete("/" + id)
                .then()
                .statusCode(204);

        given()
                .when().get("/" + id)
                .then()
                .statusCode(404);
    }
}