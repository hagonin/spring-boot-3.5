package fr.digi.d202508.springdemo.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "ville")
public class City {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "{ville.nom.notblank}")
    @Column(name = "nom", nullable = false)
    private String name;
    
    @Min(value = 0, message = "{ville.population.min}")
    @Column(name = "nb_habs", nullable = false)
    private Integer population;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_dept", nullable = false)
    @NotNull(message = "{ville.departement.required}")
    @JsonBackReference
    private Department department;

    public City() {
    }

    public City(String name, Integer population, Department department) {
        this.name = name;
        this.population = population;
        this.department = department;
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}