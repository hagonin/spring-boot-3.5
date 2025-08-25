package fr.digi.d202508.springdemo.mappers;

import fr.digi.d202508.springdemo.dtos.DepartmentDto;
import fr.digi.d202508.springdemo.entities.Department;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour la conversion entre entités Department et DepartmentDto
 */
@Component
public class DepartmentMapper {

    /**
     * Convertit une entité Department vers un DepartmentDto
     * @param department l'entité département
     * @return le DTO correspondant
     */
    public DepartmentDto toDto(Department department) {
        if (department == null) {
            return null;
        }
        
        Integer population = null;
        List<String> cityNames = null;
        
        if (department.getCities() != null) {
            population = department.getCities().stream()
                    .mapToInt(city -> city.getPopulation() != null ? city.getPopulation() : 0)
                    .sum();
            
            cityNames = department.getCities().stream()
                    .map(city -> city.getName())
                    .collect(Collectors.toList());
        }
        
        return new DepartmentDto(
            department.getId(),
            department.getCode(),
            department.getNom(),
            population,
            cityNames
        );
    }

    /**
     * Convertit un DepartmentDto vers une entité Department
     * @param departmentDto le DTO département
     * @return l'entité correspondante
     */
    public Department toEntity(DepartmentDto departmentDto) {
        if (departmentDto == null) {
            return null;
        }
        
        Department department = new Department();
        department.setId(departmentDto.getId());
        department.setCode(departmentDto.getCode());
        department.setNom(departmentDto.getName());
        
        return department;
    }

    /**
     * Met à jour une entité Departement existante avec les données d'un DepartementDto
     * @param departmentDto le DTO avec les nouvelles données
     * @param department l'entité existante à mettre à jour
     */
    public void updateEntityFromDto(DepartmentDto departmentDto, Department department) {
        if (departmentDto == null || department == null) {
            return;
        }

        department.setCode(departmentDto.getCode());
        department.setNom(departmentDto.getName());
    }

    /**
     * Convertit une liste d'entités Department vers une liste de DepartmentDto
     * @param departments la liste des entités
     * @return la liste des DTOs correspondants
     */
    public List<DepartmentDto> toDtoList(List<Department> departments) {
        if (departments == null) {
            return null;
        }
        
        return departments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}