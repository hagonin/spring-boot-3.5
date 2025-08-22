package fr.digi.d202508.springdemo.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entité JPA représentant une ville avec validation des champs pour les opérations REST.
 */
@Entity
@Table(name = "ville")
public class Ville {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "{ville.nom.notblank}")
    @Column(nullable = false)
    private String nom;
    
    @Min(value = 0, message = "{ville.population.min}")
    @Column(name = "nb_habs", nullable = false)
    private Integer population;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_dept", nullable = false)
    @NotNull(message = "{ville.departement.required}")
    @JsonBackReference
    private Departement departement;

    public Ville() {
    }

    public Ville(String nom, Integer population, Departement departement) {
        this.nom = nom;
        this.population = population;
        this.departement = departement;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }
}