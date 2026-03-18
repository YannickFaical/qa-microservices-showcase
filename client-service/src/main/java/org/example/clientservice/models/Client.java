package org.example.clientservice.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;


    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2 ,max = 100)
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    @Column(unique = true)
    private String email;

    @NotBlank
    private String telephone;

     private StatutClient statut = StatutClient.ACTIF;


     public enum StatutClient {
         ACTIF, INACTIF, SUSPENDU
     }

}
