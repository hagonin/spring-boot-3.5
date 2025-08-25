package fr.digi.d202508.springdemo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

/**
 * DTO pour la représentation des départements dans les échanges API
 */
public class DepartmentDto {
    
    private Long id;
    
    @NotBlank(message = "{departement.code.notblank}")
    @Pattern(regexp = "^[0-9A-B]{2,3}$", message = "{departement.code.pattern}")
    private String code;
    
    @NotBlank(message = "{departement.nom.notblank}")
    private String name;
    
    private Integer population;
    
    private List<String> cities;

    public DepartmentDto() {
    }

    public DepartmentDto(Long id, String code, String name, Integer population, List<String> cities) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.population = population;
        this.cities = cities;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    @Override
    public String toString() {
        return "DepartmentDto{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", population=" + population +
                ", cities=" + cities +
                '}';
    }
}