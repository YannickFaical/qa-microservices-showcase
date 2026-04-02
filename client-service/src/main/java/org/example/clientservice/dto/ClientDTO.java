package org.example.clientservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clientservice.models.Client;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String telephone;

    private Client.StatutClient statut;


    public String getEmail(){
        return this.email;
    }
    public String getNom(){
        return this.nom;
    }

    public String getTelephone(){
        return this.telephone;
    }

    public Client.StatutClient getStatut(){
        return this.statut;
    }


public static ClientDTO fromEntity(Client c){

    return ClientDTO.builder()
            .id(c.getId())
                    .nom(c.getNom())
                            .email(c.getEmail())
                                    .telephone(c.getTelephone())
                                            .statut(c.getStatut()).build();


}

public Client toEntity(){
    return Client.builder()
            .nom(this.nom)
            .email(this.email)
            .telephone(this.telephone)
            .statut(this.statut == null ? Client.StatutClient.ACTIF : this.statut).build();
}











}
