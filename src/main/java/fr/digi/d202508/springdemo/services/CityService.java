package fr.digi.d202508.springdemo.services;

import fr.digi.d202508.springdemo.daos.CityDao;
import fr.digi.d202508.springdemo.daos.DepartmentDao;
import fr.digi.d202508.springdemo.dtos.CityDto;
import fr.digi.d202508.springdemo.entities.City;
import fr.digi.d202508.springdemo.entities.Department;
import fr.digi.d202508.springdemo.mappers.CityMapper;
import fr.digi.d202508.springdemo.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service gérant la logique métier des opérations sur les villes.
 */
@Service
@Transactional(readOnly = true)
public class CityService {

    @Autowired
    private CityDao cityDao;
    
    @Autowired
    private CityRepository cityRepository;
    
    @Autowired
    private DepartmentDao departmentDao;
    
    @Autowired
    private CityMapper cityMapper;

    /**
     * Extrait et retourne la liste de toutes les villes.
     * @return la liste des DTOs des villes
     */
    public List<CityDto> getAllCities() {
        List<City> cities = cityDao.findAll();
        return cityMapper.toDtoList(cities);
    }

    /**
     * Récupère une ville par son identifiant.
     * @param cityId identifiant de la ville
     * @return le DTO de la ville si trouvée, null sinon
     */
    public CityDto getCityById(int cityId) {
        City city = cityDao.findById((long) cityId);
        return cityMapper.toDto(city);
    }

    /**
     * Récupère une ville par son nom.
     * @param name nom de la ville
     * @return le DTO de la ville si trouvée, null sinon
     */
    public CityDto getCityByName(String name) {
        City city = cityDao.findByName(name);
        return cityMapper.toDto(city);
    }

    /**
     * Insère une nouvelle ville et retourne le DTO de la ville créée.
     * @param villeDto le DTO de la ville à insérer
     * @return le DTO de la ville créée
     */
    @Transactional
    public CityDto createCity(CityDto cityDto) {
        Department department = departmentDao.findByCode(cityDto.getDepartmentCode());
        if (department == null) {
            throw new IllegalArgumentException("Département non trouvé avec le code: " + cityDto.getDepartmentCode());
        }
        
        City city = cityMapper.toEntity(cityDto, department);
        City citySaved = cityDao.save(city);
        return cityMapper.toDto(citySaved);
    }

    /**
     * Modifie une ville par son identifiant et retourne le DTO de la ville mise à jour.
     * @param cityId identifiant de la ville à modifier
     * @param cityDto nouvelle représentation de la ville
     * @return le DTO de la ville mise à jour, null si non trouvée
     */
    @Transactional
    public CityDto updateCity(int cityId, CityDto cityDto) {
        City existingCity = cityDao.findById((long) cityId);
        if (existingCity == null) {
            return null;
        }
        
        Department department = departmentDao.findByCode(cityDto.getDepartmentCode());
        if (department == null) {
            throw new IllegalArgumentException("Département non trouvé avec le code: " + cityDto.getDepartmentCode());
        }
        
        cityMapper.updateEntityFromDto(cityDto, existingCity, department);
        City updated = cityDao.save(existingCity);
        return cityMapper.toDto(updated);
    }

    /**
     * Supprime une ville par son identifiant.
     * @param cityId identifiant de la ville à supprimer
     * @return true si la suppression a réussi, false si la ville n'existait pas
     */
    @Transactional
    public boolean deleteCity(int cityId) {
        if (cityDao.existsById((long) cityId)) {
            cityDao.deleteById((long) cityId);
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
    public List<CityDto> getTopCitiesByDepartment(String departmentCode, int n) {
        List<City> cities = cityDao.findTopCitiesByDepartmentCode(departmentCode, n);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Récupère les villes d'un département dans une tranche de population.
     * @param departementCode code du département
     * @param minPopulation population minimale
     * @param maxPopulation population maximale
     * @return la liste des DTOs des villes dans la tranche de population
     */
    public List<CityDto> getCitiesByDepartmentAndPopulationRange(String departmentCode, int minPopulation, int maxPopulation) {
        List<City> cities = cityDao.findCitiesByDepartmentCodeAndPopulationRange(departmentCode, minPopulation, maxPopulation);
        return cityMapper.toDtoList(cities);
    }

    // === NOUVELLES MÉTHODES UTILISANT VILLEREPOSITORY ===

    /**
     * Recherche toutes les villes dont le nom commence par une chaîne donnée.
     * @param prefix le préfixe du nom de la ville
     * @return la liste des DTOs des villes correspondantes
     */
    public List<CityDto> getCitiesStartingWith(String prefix) {
        List<City> cities = cityRepository.findByNameStartingWithIgnoreCaseOrderByNameAsc(prefix);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Recherche toutes les villes dont la population est supérieure à un minimum.
     * @param minPopulation la population minimum
     * @return la liste des DTOs des villes triées par population décroissante
     */
    public List<CityDto> getCitiesWithMinPopulation(int minPopulation) {
        List<City> cities = cityRepository.findByPopulationGreaterThanOrderByPopulationDesc(minPopulation);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Recherche toutes les villes dont la population est comprise entre min et max.
     * @param minPopulation la population minimum (inclusive)
     * @param maxPopulation la population maximum (inclusive)
     * @return la liste des DTOs des villes triées par population décroissante
     */
    public List<CityDto> getCitiesWithPopulationBetween(int minPopulation, int maxPopulation) {
        List<City> cities = cityRepository.findByPopulationBetweenOrderByPopulationDesc(minPopulation, maxPopulation);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Recherche toutes les villes d'un département dont la population est supérieure à un minimum.
     * @param departementId l'ID du département
     * @param minPopulation la population minimum
     * @return la liste des DTOs des villes triées par population décroissante
     */
    public List<CityDto> getCitiesByDepartmentAndMinPopulation(Long departmentId, int minPopulation) {
        List<City> cities = cityRepository.findByDepartementAndPopulationGreaterThan(departmentId, minPopulation);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Recherche toutes les villes d'un département dont la population est comprise entre min et max.
     * @param departementId l'ID du département
     * @param minPopulation la population minimum (inclusive)
     * @param maxPopulation la population maximum (inclusive)
     * @return la liste des DTOs des villes triées par population décroissante
     */
    public List<CityDto> getCitiesByDepartmentAndPopulationBetween(Long departmentId, int minPopulation, int maxPopulation) {
        List<City> cities = cityRepository.findByDepartementAndPopulationBetween(departmentId, minPopulation, maxPopulation);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Recherche les n villes les plus peuplées d'un département donné par ID.
     * @param departementId l'ID du département
     * @param limit le nombre maximum de villes à retourner
     * @return la liste des DTOs des villes triées par population décroissante
     */
    public List<CityDto> getTopCitiesByDepartmentId(Long departmentId, int limit) {
        List<City> cities = cityRepository.findTopNByDepartementOrderByPopulationDesc(departmentId, limit);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Recherche par code de département avec population minimum (utilisant Repository).
     * @param departementCode le code du département
     * @param minPopulation la population minimum
     * @return la liste des DTOs des villes correspondantes
     */
    public List<CityDto> getCitiesByDepartmentCodeAndMinPopulation(String departmentCode, int minPopulation) {
        List<City> cities = cityRepository.findByDepartementCodeAndPopulationGreaterThan(departmentCode, minPopulation);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Recherche par code de département avec population entre min et max (utilisant Repository).
     * @param departementCode le code du département
     * @param minPopulation la population minimum (inclusive)
     * @param maxPopulation la population maximum (inclusive)
     * @return la liste des DTOs des villes correspondantes
     */
    public List<CityDto> getCitiesByDepartmentCodeAndPopulationBetween(String departmentCode, int minPopulation, int maxPopulation) {
        List<City> cities = cityRepository.findByDepartementCodeAndPopulationBetween(departmentCode, minPopulation, maxPopulation);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Recherche les n villes les plus peuplées d'un département par son code (utilisant Repository).
     * @param departementCode le code du département
     * @param limit le nombre maximum de villes à retourner
     * @return la liste des DTOs des villes correspondantes
     */
    public List<CityDto> getTopCitiesByDepartmentCode(String departmentCode, int limit) {
        List<City> cities = cityRepository.findTopNByDepartementCodeOrderByPopulationDesc(departmentCode, limit);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Alternative pour getCityByName utilisant VilleRepository.
     * @param name nom de la ville
     * @return le DTO de la ville si trouvée, null sinon
     */
    public CityDto getCityByNameUsingRepository(String name) {
        Optional<City> city = cityRepository.findByName(name);
        return city.map(cityMapper::toDto).orElse(null);
    }
}