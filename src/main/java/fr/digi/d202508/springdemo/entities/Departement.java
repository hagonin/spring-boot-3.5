package fr.digi.d202508.springdemo.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Entité JPA représentant un département.
 */
@Entity
@Table(name = "departement")
public class Departement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "{departement.code.notblank}")
    @Column(nullable = false, unique = true)
    private String code;
    
    @NotBlank(message = "{departement.nom.notblank}")
    private String nom;

    @OneToMany(mappedBy = "departement", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Ville> villes;

    public Departement() {
    }

    public Departement(String code, String nom) {
        this.code = code;
        this.nom = nom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Ville> getVilles() {
        return villes;
    }

    public void setVilles(List<Ville> villes) {
        this.villes = villes;
    }
}