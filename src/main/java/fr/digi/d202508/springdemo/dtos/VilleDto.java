package fr.digi.d202508.springdemo.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO pour la représentation des villes dans les échanges API
 */
public class VilleDto {
    
    private Long id;

    @NotBlank(message = "{ville.nom.notblank}")
    private String nom;
    
    @Min(value = 0, message = "{ville.population.min}")
    private Integer population;
    
    @NotNull(message = "{ville.departement.required}")
    private String codeDepartement;

    public VilleDto() {
    }

    public VilleDto(Long id, String nom, Integer population, String codeDepartement) {
        this.id = id;
        this.nom = nom;
        this.population = population;
        this.codeDepartement = codeDepartement;
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

    public String getCodeDepartement() {
        return codeDepartement;
    }

    public void setCodeDepartement(String codeDepartement) {
        this.codeDepartement = codeDepartement;
    }

    @Override
    public String toString() {
        return "VilleDto{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", population=" + population +
                ", codeDepartement='" + codeDepartement + '\'' +
                '}';
    }
}