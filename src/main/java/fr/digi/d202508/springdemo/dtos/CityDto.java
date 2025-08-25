package fr.digi.d202508.springdemo.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class CityDto {
    
    private Long id;

    @NotBlank(message = "{ville.nom.notblank}")
    private String name;
    
    @Min(value = 0, message = "{ville.population.min}")
    private Integer population;
    
    @NotNull(message = "{ville.departement.required}")
    private String departmentCode;

    public CityDto() {
    }

    public CityDto(Long id, String name, Integer population, String departmentCode) {
        this.id = id;
        this.name = name;
        this.population = population;
        this.departmentCode = departmentCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    @Override
    public String toString() {
        return "CityDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", population=" + population +
                ", departmentCode='" + departmentCode + '\'' +
                '}';
    }
}