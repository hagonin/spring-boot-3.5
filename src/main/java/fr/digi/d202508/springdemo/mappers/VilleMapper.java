package fr.digi.d202508.springdemo.mappers;

import fr.digi.d202508.springdemo.dtos.VilleDto;
import fr.digi.d202508.springdemo.entities.Ville;
import fr.digi.d202508.springdemo.entities.Departement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour la conversion entre entités Ville et VilleDto
 */
@Component
public class VilleMapper {

    /**
     * Convertit une entité Ville vers un VilleDto
     * @param ville l'entité ville
     * @return le DTO correspondant
     */
    public VilleDto toDto(Ville ville) {
        if (ville == null) {
            return null;
        }
        
        return new VilleDto(
            ville.getId(),
            ville.getNom(),
            ville.getPopulation(),
            ville.getDepartement() != null ? ville.getDepartement().getCode() : null
        );
    }

    /**
     * Convertit un VilleDto vers une entité Ville
     * @param villeDto le DTO ville
     * @param departement le département associé (doit être récupéré séparément)
     * @return l'entité correspondante
     */
    public Ville toEntity(VilleDto villeDto, Departement departement) {
        if (villeDto == null) {
            return null;
        }
        
        Ville ville = new Ville();
        ville.setId(villeDto.getId());
        ville.setNom(villeDto.getNom());
        ville.setPopulation(villeDto.getPopulation());
        ville.setDepartement(departement);
        
        return ville;
    }

    /**
     * Met à jour une entité Ville existante avec les données d'un VilleDto
     * @param villeDto le DTO avec les nouvelles données
     * @param ville l'entité existante à mettre à jour
     * @param departement le département associé
     */
    public void updateEntityFromDto(VilleDto villeDto, Ville ville, Departement departement) {
        if (villeDto == null || ville == null) {
            return;
        }
        
        ville.setNom(villeDto.getNom());
        ville.setPopulation(villeDto.getPopulation());
        ville.setDepartement(departement);
    }

    /**
     * Convertit une liste d'entités Ville vers une liste de VilleDto
     * @param villes la liste des entités
     * @return la liste des DTOs correspondants
     */
    public List<VilleDto> toDtoList(List<Ville> villes) {
        if (villes == null) {
            return null;
        }
        
        return villes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}