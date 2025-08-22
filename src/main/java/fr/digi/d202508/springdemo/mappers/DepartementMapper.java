package fr.digi.d202508.springdemo.mappers;

import fr.digi.d202508.springdemo.dtos.DepartementDto;
import fr.digi.d202508.springdemo.entities.Departement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour la conversion entre entités Departement et DepartementDto
 */
@Component
public class DepartementMapper {

    /**
     * Convertit une entité Departement vers un DepartementDto
     * @param departement l'entité département
     * @return le DTO correspondant
     */
    public DepartementDto toDto(Departement departement) {
        if (departement == null) {
            return null;
        }
        
        Integer population = null;
        if (departement.getVilles() != null) {
            population = departement.getVilles().stream()
                    .mapToInt(ville -> ville.getPopulation() != null ? ville.getPopulation() : 0)
                    .sum();
        }
        
        return new DepartementDto(
            departement.getId(),
            departement.getCode(),
            departement.getNom(),
            population
        );
    }

    /**
     * Convertit un DepartementDto vers une entité Departement
     * @param departementDto le DTO département
     * @return l'entité correspondante
     */
    public Departement toEntity(DepartementDto departementDto) {
        if (departementDto == null) {
            return null;
        }
        
        Departement departement = new Departement();
        departement.setId(departementDto.getId());
        departement.setCode(departementDto.getCode());
        departement.setNom(departementDto.getNom());
        
        return departement;
    }

    /**
     * Met à jour une entité Departement existante avec les données d'un DepartementDto
     * @param departementDto le DTO avec les nouvelles données
     * @param departement l'entité existante à mettre à jour
     */
    public void updateEntityFromDto(DepartementDto departementDto, Departement departement) {
        if (departementDto == null || departement == null) {
            return;
        }
        
        departement.setCode(departementDto.getCode());
        departement.setNom(departementDto.getNom());
    }

    /**
     * Convertit une liste d'entités Departement vers une liste de DepartementDto
     * @param departements la liste des entités
     * @return la liste des DTOs correspondants
     */
    public List<DepartementDto> toDtoList(List<Departement> departements) {
        if (departements == null) {
            return null;
        }
        
        return departements.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}