package fr.digi.d202508.springdemo.mappers;

import fr.digi.d202508.springdemo.dtos.CityDto;
import fr.digi.d202508.springdemo.entities.City;
import fr.digi.d202508.springdemo.entities.Department;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour la conversion entre entités City et CityDto
 */
@Component
public class CityMapper {

    /**
     * Convertit une entité City vers un CityDto
     * @param city l'entité ville
     * @return le DTO correspondant
     */
    public CityDto toDto(City city) {
        if (city == null) {
            return null;
        }
        
        return new CityDto(
            city.getId(),
            city.getName(),
            city.getPopulation(),
            city.getDepartment() != null ? city.getDepartment().getCode() : null
        );
    }

    /**
     * Convertit un CityDto vers une entité City
     * @param cityDto le DTO ville
     * @param department le département associé (doit être récupéré séparément)
     * @return l'entité correspondante
     */
    public City toEntity(CityDto cityDto, Department department) {
        if (cityDto == null) {
            return null;
        }
        
        City city = new City();
        city.setId(cityDto.getId());
        city.setName(cityDto.getName());
        city.setPopulation(cityDto.getPopulation());
        city.setDepartment(department);
        
        return city;
    }

    /**
     * Met à jour une entité City existante avec les données d'un CityDto
     * @param cityDto le DTO avec les nouvelles données
     * @param city l'entité existante à mettre à jour
     * @param department le département associé
     */
    public void updateEntityFromDto(CityDto cityDto, City city, Department department) {
        if (cityDto == null || city == null) {
            return;
        }
        
        city.setName(cityDto.getName());
        city.setPopulation(cityDto.getPopulation());
        city.setDepartment(department);
    }

    /**
     * Convertit une liste d'entités City vers une liste de CityDto
     * @param cities la liste des entités
     * @return la liste des DTOs correspondants
     */
    public List<CityDto> toDtoList(List<City> cities) {
        if (cities == null) {
            return null;
        }
        
        return cities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}