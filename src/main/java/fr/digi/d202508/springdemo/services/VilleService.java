package fr.digi.d202508.springdemo.services;

import fr.digi.d202508.springdemo.daos.VilleDao;
import fr.digi.d202508.springdemo.daos.DepartementDao;
import fr.digi.d202508.springdemo.dtos.VilleDto;
import fr.digi.d202508.springdemo.entities.Ville;
import fr.digi.d202508.springdemo.entities.Departement;
import fr.digi.d202508.springdemo.mappers.VilleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service gérant la logique métier des opérations sur les villes.
 */
@Service
@Transactional(readOnly = true)
public class VilleService {

    @Autowired
    private VilleDao villeDao;
    
    @Autowired
    private DepartementDao departementDao;
    
    @Autowired
    private VilleMapper villeMapper;

    /**
     * Extrait et retourne la liste de toutes les villes.
     * @return la liste des DTOs des villes
     */
    public List<VilleDto> getAllCities() {
        List<Ville> villes = villeDao.findAll();
        return villeMapper.toDtoList(villes);
    }

    /**
     * Récupère une ville par son identifiant.
     * @param idVille identifiant de la ville
     * @return le DTO de la ville si trouvée, null sinon
     */
    public VilleDto getCityById(int idVille) {
        Ville ville = villeDao.findById((long) idVille);
        return villeMapper.toDto(ville);
    }

    /**
     * Récupère une ville par son nom.
     * @param nom nom de la ville
     * @return le DTO de la ville si trouvée, null sinon
     */
    public VilleDto getCityByName(String nom) {
        Ville ville = villeDao.findByName(nom);
        return villeMapper.toDto(ville);
    }

    /**
     * Insère une nouvelle ville et retourne le DTO de la ville créée.
     * @param villeDto le DTO de la ville à insérer
     * @return le DTO de la ville créée
     */
    @Transactional
    public VilleDto createCity(VilleDto villeDto) {
        Departement departement = departementDao.findByCode(villeDto.getCodeDepartement());
        if (departement == null) {
            throw new IllegalArgumentException("Département non trouvé avec le code: " + villeDto.getCodeDepartement());
        }
        
        Ville ville = villeMapper.toEntity(villeDto, departement);
        Ville villeSauvee = villeDao.save(ville);
        return villeMapper.toDto(villeSauvee);
    }

    /**
     * Modifie une ville par son identifiant et retourne le DTO de la ville mise à jour.
     * @param idVille identifiant de la ville à modifier
     * @param villeDto nouvelle représentation de la ville
     * @return le DTO de la ville mise à jour, null si non trouvée
     */
    @Transactional
    public VilleDto updateCity(int idVille, VilleDto villeDto) {
        Ville villeExistante = villeDao.findById((long) idVille);
        if (villeExistante == null) {
            return null;
        }
        
        Departement departement = departementDao.findByCode(villeDto.getCodeDepartement());
        if (departement == null) {
            throw new IllegalArgumentException("Département non trouvé avec le code: " + villeDto.getCodeDepartement());
        }
        
        villeMapper.updateEntityFromDto(villeDto, villeExistante, departement);
        Ville villeMiseAJour = villeDao.save(villeExistante);
        return villeMapper.toDto(villeMiseAJour);
    }

    /**
     * Supprime une ville par son identifiant.
     * @param idVille identifiant de la ville à supprimer
     * @return true si la suppression a réussi, false si la ville n'existait pas
     */
    @Transactional
    public boolean deleteCity(int idVille) {
        if (villeDao.existsById((long) idVille)) {
            villeDao.deleteById((long) idVille);
            return true;
        }
        return false;
    }

    /**
     * Récupère les N villes les plus peuplées d'un département.
     * @param departementCode code du département
     * @param n nombre de villes à récupérer
     * @return la liste des DTOs des villes triées par population décroissante
     */
    public List<VilleDto> getTopCitiesByDepartment(String departementCode, int n) {
        List<Ville> villes = villeDao.findTopCitiesByDepartementCode(departementCode, n);
        return villeMapper.toDtoList(villes);
    }

    /**
     * Récupère les villes d'un département dans une tranche de population.
     * @param departementCode code du département
     * @param minPopulation population minimale
     * @param maxPopulation population maximale
     * @return la liste des DTOs des villes dans la tranche de population
     */
    public List<VilleDto> getCitiesByDepartmentAndPopulationRange(String departementCode, int minPopulation, int maxPopulation) {
        List<Ville> villes = villeDao.findCitiesByDepartementCodeAndPopulationRange(departementCode, minPopulation, maxPopulation);
        return villeMapper.toDtoList(villes);
    }
}