package org.example.clientservice.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter@Setter
    private Long Id;


    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2 ,max = 100)

    @Setter
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    @Column(unique = true)
    @Getter@Setter

    private String email;

    @NotBlank
    @Getter@Setter

    private String telephone;

    @Getter@Setter
    @Enumerated(EnumType.STRING)
     private StatutClient statut = StatutClient.ACTIF;

    public String getNom(){
        return this.nom;
    }

    public void setNom(String nom){
        this.nom=nom;
    }
    public void setEmail(String email){
        this.email=email;
    }

    public void setTelephone(String telephone){
        this.telephone=telephone;
    }

    public StatutClient getStatut(){
        return this.statut;
    }

    public void setStatut(StatutClient statut){
        this.statut =statut;

    }






     public enum StatutClient {
         ACTIF, INACTIF, SUSPENDU
     }

}
