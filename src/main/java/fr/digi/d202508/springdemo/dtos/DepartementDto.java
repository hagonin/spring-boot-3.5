package fr.digi.d202508.springdemo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

/**
 * DTO pour la représentation des départements dans les échanges API
 */
public class DepartementDto {
    
    private Long id;
    
    @NotBlank(message = "{departement.code.notblank}")
    @Pattern(regexp = "^[0-9A-B]{2,3}$", message = "{departement.code.pattern}")
    private String code;
    
    @NotBlank(message = "{departement.nom.notblank}")
    private String nom;
    
    private Integer population;
    
    private List<String> villes;

    public DepartementDto() {
    }

    public DepartementDto(Long id, String code, String nom, Integer population, List<String> villes) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.population = population;
        this.villes = villes;
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

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public List<String> getVilles() {
        return villes;
    }

    public void setVilles(List<String> villes) {
        this.villes = villes;
    }

    @Override
    public String toString() {
        return "DepartementDto{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", nom='" + nom + '\'' +
                ", population=" + population +
                ", villes=" + villes +
                '}';
    }
}