package fr.digi.d202508.springdemo.services;

import fr.digi.d202508.springdemo.daos.CityDao;
import fr.digi.d202508.springdemo.daos.DepartmentDao;
import fr.digi.d202508.springdemo.dtos.CityDto;
import fr.digi.d202508.springdemo.entities.City;
import fr.digi.d202508.springdemo.entities.Department;
import fr.digi.d202508.springdemo.exceptions.ApplicationException;
import fr.digi.d202508.springdemo.mappers.CityMapper;
import fr.digi.d202508.springdemo.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
    
    @Autowired
    private MessageSource messageSource;

    /**
     * Extrait et retourne la liste de toutes les villes
     * @return la liste des DTOs des villes
     */
    public List<CityDto> getAllCities() {
        List<City> cities = cityDao.findAll();
        return cityMapper.toDtoList(cities);
    }

    /**
     * Récupère une ville par son identifiant
     * @param cityId identifiant de la ville
     * @return le DTO de la ville si trouvée
     * @throws ApplicationException si la ville n'existe pas ou si l'ID est invalide
     */
    public CityDto getCityById(int cityId) throws ApplicationException {
        if (cityId <= 0) {
            throw new ApplicationException(messageSource.getMessage("ville.id.positive", null, LocaleContextHolder.getLocale()));
        }
        City city = cityDao.findById((long) cityId);
        if (city == null) {
            throw new ApplicationException(messageSource.getMessage("ville.not.found.id", new Object[]{cityId}, LocaleContextHolder.getLocale()));
        }
        return cityMapper.toDto(city);
    }

    /**
     * Récupère une ville par son nom
     * @param name nom de la ville
     * @return le DTO de la ville si trouvée
     * @throws ApplicationException si la ville n'existe pas ou si le nom est invalide
     */
    public CityDto getCityByName(String name) throws ApplicationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ApplicationException(messageSource.getMessage("ville.nom.notblank", null, LocaleContextHolder.getLocale()));
        }
        if (name.trim().length() < 2) {
            throw new ApplicationException(messageSource.getMessage("ville.name.min.length", null, LocaleContextHolder.getLocale()));
        }
        City city = cityDao.findByName(name.trim());
        if (city == null) {
            throw new ApplicationException(messageSource.getMessage("ville.not.found.name", new Object[]{name}, LocaleContextHolder.getLocale()));
        }
        return cityMapper.toDto(city);
    }

    /**
     * Insère une nouvelle ville et retourne le DTO de la ville créée
     * @param cityDto le DTO de la ville à insérer
     * @return le DTO de la ville créée
     * @throws ApplicationException si les données sont invalides
     */
    @Transactional
    public CityDto createCity(CityDto cityDto) throws ApplicationException {
        validateCityData(cityDto);
        
        // Vérifier que la ville n'existe pas déjà
        City existingCity = cityDao.findByName(cityDto.getName().trim());
        if (existingCity != null) {
            throw new ApplicationException(messageSource.getMessage("ville.name.duplicate", new Object[]{cityDto.getName()}, LocaleContextHolder.getLocale()));
        }
        
        Department department = departmentDao.findByCode(cityDto.getDepartmentCode());
        if (department == null) {
            throw new ApplicationException(messageSource.getMessage("departement.not.found.code", new Object[]{cityDto.getDepartmentCode()}, LocaleContextHolder.getLocale()));
        }
        
        City city = cityMapper.toEntity(cityDto, department);
        City citySaved = cityDao.save(city);
        return cityMapper.toDto(citySaved);
    }

    /**
     * Modifie une ville par son identifiant et retourne le DTO de la ville mise à jour
     * @param cityId identifiant de la ville à modifier
     * @param cityDto nouvelle représentation de la ville
     * @return le DTO de la ville mise à jour
     * @throws ApplicationException si les données sont invalides ou la ville n'existe pas
     */
    @Transactional
    public CityDto updateCity(int cityId, CityDto cityDto) throws ApplicationException {
        if (cityId <= 0) {
            throw new ApplicationException(messageSource.getMessage("ville.id.positive", null, LocaleContextHolder.getLocale()));
        }
        
        validateCityData(cityDto);
        
        City existingCity = cityDao.findById((long) cityId);
        if (existingCity == null) {
            throw new ApplicationException(messageSource.getMessage("ville.not.found.id", new Object[]{cityId}, LocaleContextHolder.getLocale()));
        }
        
        // Vérifier qu'aucune autre ville n'a le même nom
        City cityWithSameName = cityDao.findByName(cityDto.getName().trim());
        if (cityWithSameName != null && !cityWithSameName.getId().equals(existingCity.getId())) {
            throw new ApplicationException(messageSource.getMessage("ville.name.duplicate.update", new Object[]{cityDto.getName()}, LocaleContextHolder.getLocale()));
        }
        
        Department department = departmentDao.findByCode(cityDto.getDepartmentCode());
        if (department == null) {
            throw new ApplicationException(messageSource.getMessage("departement.not.found.code", new Object[]{cityDto.getDepartmentCode()}, LocaleContextHolder.getLocale()));
        }
        
        cityMapper.updateEntityFromDto(cityDto, existingCity, department);
        City updated = cityDao.save(existingCity);
        return cityMapper.toDto(updated);
    }

    /**
     * Supprime une ville par son identifiant.
     * @param cityId identifiant de la ville à supprimer
     * @return true si la suppression a réussi
     * @throws ApplicationException si l'ID est invalide ou la ville n'existe pas
     */
    @Transactional
    public boolean deleteCity(int cityId) throws ApplicationException {
        if (cityId <= 0) {
            throw new ApplicationException(messageSource.getMessage("ville.id.positive", null, LocaleContextHolder.getLocale()));
        }
        if (!cityDao.existsById((long) cityId)) {
            throw new ApplicationException(messageSource.getMessage("ville.not.found.id", new Object[]{cityId}, LocaleContextHolder.getLocale()));
        }
        cityDao.deleteById((long) cityId);
        return true;
    }

    /**
     * Récupère les N villes les plus peuplées d'un département.
     * @param departmentCode code du département
     * @param n nombre de villes à récupérer
     * @return la liste des DTOs des villes triées par population décroissante
     * @throws ApplicationException si les paramètres sont invalides
     */
    public List<CityDto> getTopCitiesByDepartment(String departmentCode, int n) throws ApplicationException {
        if (departmentCode == null || departmentCode.trim().isEmpty()) {
            throw new ApplicationException(messageSource.getMessage("departement.code.notblank", null, LocaleContextHolder.getLocale()));
        }
        if (n <= 0) {
            throw new ApplicationException(messageSource.getMessage("ville.count.positive", null, LocaleContextHolder.getLocale()));
        }
        if (n > 1000) {
            throw new ApplicationException(messageSource.getMessage("ville.count.max.exceeded", null, LocaleContextHolder.getLocale()));
        }
        
        Department department = departmentDao.findByCode(departmentCode.trim());
        if (department == null) {
            throw new ApplicationException(messageSource.getMessage("departement.not.found.code", new Object[]{departmentCode}, LocaleContextHolder.getLocale()));
        }
        
        List<City> cities = cityDao.findTopCitiesByDepartmentCode(departmentCode.trim(), n);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Récupère les villes d'un département dans une tranche de population.
     * @param departmentCode code du département
     * @param minPopulation population minimale
     * @param maxPopulation population maximale
     * @return la liste des DTOs des villes dans la tranche de population
     * @throws ApplicationException si les paramètres sont invalides
     */
    public List<CityDto> getCitiesByDepartmentAndPopulationRange(String departmentCode, int minPopulation, int maxPopulation) throws ApplicationException {
        if (departmentCode == null || departmentCode.trim().isEmpty()) {
            throw new ApplicationException(messageSource.getMessage("departement.code.notblank", null, LocaleContextHolder.getLocale()));
        }
        validatePopulationRange(minPopulation, maxPopulation);
        
        Department department = departmentDao.findByCode(departmentCode.trim());
        if (department == null) {
            throw new ApplicationException(messageSource.getMessage("departement.not.found.code", new Object[]{departmentCode}, LocaleContextHolder.getLocale()));
        }
        
        List<City> cities = cityDao.findCitiesByDepartmentCodeAndPopulationRange(departmentCode.trim(), minPopulation, maxPopulation);
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
     * @param departmentId l'ID du département
     * @param minPopulation la population minimum
     * @return la liste des DTOs des villes triées par population décroissante
     */
    public List<CityDto> getCitiesByDepartmentAndMinPopulation(Long departmentId, int minPopulation) {
        List<City> cities = cityRepository.findByDepartementAndPopulationGreaterThan(departmentId, minPopulation);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Recherche toutes les villes d'un département dont la population est comprise entre min et max.
     * @param departmentId l'ID du département
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
     * @param departmentId l'ID du département
     * @param limit le nombre maximum de villes à retourner
     * @return la liste des DTOs des villes triées par population décroissante
     */
    public List<CityDto> getTopCitiesByDepartmentId(Long departmentId, int limit) {
        List<City> cities = cityRepository.findTopNByDepartementOrderByPopulationDesc(departmentId, limit);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Recherche par code de département avec population minimum (utilisant Repository).
     * @param departmentCode le code du département
     * @param minPopulation la population minimum
     * @return la liste des DTOs des villes correspondantes
     */
    public List<CityDto> getCitiesByDepartmentCodeAndMinPopulation(String departmentCode, int minPopulation) {
        List<City> cities = cityRepository.findByDepartementCodeAndPopulationGreaterThan(departmentCode, minPopulation);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Recherche par code de département avec population entre min et max (utilisant Repository).
     * @param departmentCode le code du département
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
     * @param departmentCode le code du département
     * @param limit le nombre maximum de villes à retourner
     * @return la liste des DTOs des villes correspondantes
     */
    public List<CityDto> getTopCitiesByDepartmentCode(String departmentCode, int limit) {
        List<City> cities = cityRepository.findTopNByDepartementCodeOrderByPopulationDesc(departmentCode, limit);
        return cityMapper.toDtoList(cities);
    }

    /**
     * Valide les données d'une ville
     * @param cityDto les données de la ville à valider
     * @throws ApplicationException si les données sont invalides
     */
    private void validateCityData(CityDto cityDto) throws ApplicationException {
        if (cityDto == null) {
            throw new ApplicationException("Les données de la ville ne peuvent pas être nulles");
        }
        
        if (cityDto.getName() == null || cityDto.getName().trim().isEmpty()) {
            throw new ApplicationException(messageSource.getMessage("ville.nom.notblank", null, LocaleContextHolder.getLocale()));
        }
        
        if (cityDto.getName().trim().length() < 2) {
            throw new ApplicationException(messageSource.getMessage("ville.nom.size", null, LocaleContextHolder.getLocale()));
        }
        
        if (cityDto.getPopulation() == null || cityDto.getPopulation() < 0) {
            throw new ApplicationException(messageSource.getMessage("ville.population.negative", null, LocaleContextHolder.getLocale()));
        }
        
        if (cityDto.getPopulation() > 50000000) {
            throw new ApplicationException(messageSource.getMessage("ville.population.max.exceeded", null, LocaleContextHolder.getLocale()));
        }
        
        if (cityDto.getDepartmentCode() == null || cityDto.getDepartmentCode().trim().isEmpty()) {
            throw new ApplicationException(messageSource.getMessage("ville.departement.required", null, LocaleContextHolder.getLocale()));
        }
    }

    /**
     * Valide une tranche de population
     * @param minPopulation population minimale
     * @param maxPopulation population maximale
     * @throws ApplicationException si la tranche est invalide
     */
    private void validatePopulationRange(int minPopulation, int maxPopulation) throws ApplicationException {
        if (minPopulation < 0) {
            throw new ApplicationException(messageSource.getMessage("ville.population.negative", null, LocaleContextHolder.getLocale()));
        }
        if (maxPopulation < 0) {
            throw new ApplicationException(messageSource.getMessage("ville.population.negative", null, LocaleContextHolder.getLocale()));
        }
        if (minPopulation > maxPopulation) {
            throw new ApplicationException(messageSource.getMessage("ville.population.range.invalid", new Object[]{minPopulation, maxPopulation}, LocaleContextHolder.getLocale()));
        }
    }

}