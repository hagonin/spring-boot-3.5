package fr.digi.d202508.springdemo.controllers;

import fr.digi.d202508.springdemo.dtos.CityDto;
import fr.digi.d202508.springdemo.services.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cities")
public class CityController {

    @Autowired
    private CityService cityService;
    
    @Autowired
    private MessageSource messageSource;

    /**
     * Valide la requête et retourne une réponse d'erreur le cas échéant
     * @param result résultat de la validation
     * @return une réponse 400 si erreurs, sinon null
     */
    private ResponseEntity<String> validateCity(BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            result.getFieldErrors().forEach(error ->
                errors.append(error.getDefaultMessage()).append("; ")
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }
        return null;
    }

    /**
     * Récupère la liste complète des villes
     * @return la liste des villes
     */
    @GetMapping
    public List<CityDto> getCities() {
        return cityService.getAllCities();
    }

    /**
     * Récupère une ville par son identifiant
     * @param id identifiant de la ville
     * @return 200 avec la ville si trouvée, 404 sinon
     */
    @GetMapping("/{id}")
    public ResponseEntity<CityDto> getCityById(@PathVariable int id) {
        CityDto city = cityService.getCityById(id);
        if (city != null) {
            return ResponseEntity.ok(city);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Récupère une ville par son nom
     * @param name nom de la ville
     * @return 200 avec la ville si trouvée, 404 sinon
     */
    @GetMapping("/city-name/{name}")
    public ResponseEntity<CityDto> getCityByName(@PathVariable String name) {
        CityDto city = cityService.getCityByName(name);
        if (city != null) {
            return ResponseEntity.ok(city);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Ajoute une nouvelle ville après validation
     * @param newCity la ville à ajouter
     * @param result résultat de la validation de la requête
     * @return 201 si ajout réussi, 400 si erreurs de validation ou doublons
     */
    @PostMapping
    public ResponseEntity<?> createCity(@Valid @RequestBody CityDto newCity, BindingResult result) {
        ResponseEntity<String> validationError = validateCity(result);
        if (validationError != null) {
            return validationError;
        }
        
        // Vérifier si la ville existe déjà par nom
        if (cityService.getCityByName(newCity.getName()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(messageSource.getMessage("ville.nom.exists", null, LocaleContextHolder.getLocale()));
        }
        
        try {
            CityDto createdCity = cityService.createCity(newCity);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Met à jour une ville existante
     * @param id identifiant de la ville à modifier
     * @param updatedCity nouvelle représentation de la ville
     * @param result résultat de la validation
     * @return 200 si succès, 400 si erreurs de validation, 404 si la ville n'existe pas
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCity(@PathVariable int id, @Valid @RequestBody CityDto updatedCity, BindingResult result) {
        ResponseEntity<String> validationError = validateCity(result);
        if (validationError != null) {
            return validationError;
        }
        
        if (cityService.getCityById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            CityDto updatedCityDto = cityService.updateCity(id, updatedCity);
            if (updatedCityDto != null) {
                return ResponseEntity.ok(updatedCityDto);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Supprime une ville par son identifiant
     * @param id identifiant de la ville à supprimer
     * @return 200 si supprimée, 404 si introuvable
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable int id) {
        boolean removed = cityService.deleteCity(id);
        if (removed) {
            return ResponseEntity.ok("Ville supprimée avec succès");
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Récupère les N villes les plus peuplées d'un département
     * @param departmentCode code du département
     * @param n nombre de villes à récupérer
     * @return la liste des villes triées par population décroissante
     */
    @GetMapping("/top")
    public ResponseEntity<List<CityDto>> getTopCitiesByDepartment(
            @RequestParam String departmentCode, 
            @RequestParam int n) {
        List<CityDto> cities = cityService.getTopCitiesByDepartment(departmentCode, n);
        return ResponseEntity.ok(cities);
    }

    /**
     * Récupère les villes d'un département dans une tranche de population
     * @param departmentCode code du département
     * @param min population minimale
     * @param max population maximale
     * @return la liste des villes dans la tranche de population
     */
    @GetMapping("/by-population")
    public ResponseEntity<List<CityDto>> getCitiesByPopulationRange(
            @RequestParam String departmentCode,
            @RequestParam int min,
            @RequestParam int max) {
        List<CityDto> cities = cityService.getCitiesByDepartmentAndPopulationRange(departmentCode, min, max);
        return ResponseEntity.ok(cities);
    }

    // === NOUVELLES ROUTES UTILISANT VILLEREPOSITORY ===

    /**
     * Recherche toutes les villes dont le nom commence par une chaîne donnée
     * @param prefix le préfixe du nom de la ville
     * @return la liste des villes correspondantes
     */
    @GetMapping("/starts-with/{prefix}")
    public ResponseEntity<List<CityDto>> getCitiesStartingWith(@PathVariable String prefix) {
        List<CityDto> cities = cityService.getCitiesStartingWith(prefix);
        return ResponseEntity.ok(cities);
    }

    /**
     * Recherche toutes les villes dont la population est supérieure à un minimum
     * @param minPopulation la population minimum
     * @return la liste des villes triées par population décroissante
     */
    @GetMapping("/population-min")
    public ResponseEntity<List<CityDto>> getCitiesWithMinPopulation(@RequestParam int minPopulation) {
        List<CityDto> cities = cityService.getCitiesWithMinPopulation(minPopulation);
        return ResponseEntity.ok(cities);
    }

    /**
     * Recherche toutes les villes dont la population est comprise entre min et max
     * @param minPopulation la population minimum (inclusive)
     * @param maxPopulation la population maximum (inclusive)
     * @return la liste des villes triées par population décroissante
     */
    @GetMapping("/population-between")
    public ResponseEntity<List<CityDto>> getCitiesWithPopulationBetween(
            @RequestParam int minPopulation, 
            @RequestParam int maxPopulation) {
        List<CityDto> cities = cityService.getCitiesWithPopulationBetween(minPopulation, maxPopulation);
        return ResponseEntity.ok(cities);
    }

    /**
     * Recherche toutes les villes d'un département (par ID) dont la population est supérieure à un minimum
     * @param departmentId l'ID du département
     * @param minPopulation la population minimum
     * @return la liste des villes triées par population décroissante
     */
    @GetMapping("/department/{departmentId}/population-min")
    public ResponseEntity<List<CityDto>> getCitiesByDepartmentAndMinPopulation(
            @PathVariable Long departmentId,
            @RequestParam int minPopulation) {
        List<CityDto> cities = cityService.getCitiesByDepartmentAndMinPopulation(departmentId, minPopulation);
        return ResponseEntity.ok(cities);
    }

    /**
     * Recherche toutes les villes d'un département (par ID) dont la population est comprise entre min et max
     * @param departmentId l'ID du département
     * @param minPopulation la population minimum (inclusive)
     * @param maxPopulation la population maximum (inclusive)
     * @return la liste des villes triées par population décroissante
     */
    @GetMapping("/departement/{departementId}/population-between")
    public ResponseEntity<List<CityDto>> getCitiesByDepartmentAndPopulationBetween(
            @PathVariable Long departmentId,
            @RequestParam int minPopulation,
            @RequestParam int maxPopulation) {
        List<CityDto> cities = cityService.getCitiesByDepartmentAndPopulationBetween(departmentId, minPopulation, maxPopulation);
        return ResponseEntity.ok(cities);
    }

    /**
     * Recherche les n villes les plus peuplées d'un département donné (par ID)
     * @param departmentId l'ID du département
     * @param limit le nombre maximum de villes à retourner
     * @return la liste des villes triées par population décroissante
     */
    @GetMapping("/department/{departmentId}/top-cities")
    public ResponseEntity<List<CityDto>> getTopCitiesByDepartmentId(
            @PathVariable Long departmentId,
            @RequestParam int limit) {
        List<CityDto> cities = cityService.getTopCitiesByDepartmentId(departmentId, limit);
        return ResponseEntity.ok(cities);
    }

    /**
     * Recherche par code de département avec population minimum
     * @param departmentCode le code du département
     * @param minPopulation la population minimum
     * @return la liste des villes correspondantes
     */
    @GetMapping("/code/{departmentCode}/population-min")
    public ResponseEntity<List<CityDto>> getCitiesByDepartmentCodeAndMinPopulation(
            @PathVariable String departmentCode,
            @RequestParam int minPopulation) {
        List<CityDto> cities = cityService.getCitiesByDepartmentCodeAndMinPopulation(departmentCode, minPopulation);
        return ResponseEntity.ok(cities);
    }

    /**
     * Recherche par code de département avec population entre min et max
     * @param departmentCode le code du département
     * @param minPopulation la population minimum (inclusive)
     * @param maxPopulation la population maximum (inclusive)
     * @return la liste des villes correspondantes
     */
    @GetMapping("/code/{departmentCode}/population-between")
    public ResponseEntity<List<CityDto>> getCitiesByDepartmentCodeAndPopulationBetween(
            @PathVariable String departmentCode,
            @RequestParam int minPopulation,
            @RequestParam int maxPopulation) {
        List<CityDto> cities = cityService.getCitiesByDepartmentCodeAndPopulationBetween(departmentCode, minPopulation, maxPopulation);
        return ResponseEntity.ok(cities);
    }

    /**
     * Recherche les n villes les plus peuplées d'un département par son code
     * @param departmentCode le code du département
     * @param limit le nombre maximum de villes à retourner
     * @return la liste des villes correspondantes
     */
    @GetMapping("/code/{departmentCode}/top-cities")
    public ResponseEntity<List<CityDto>> getTopCitiesByDepartmentCode(
            @PathVariable String departmentCode,
            @RequestParam int limit) {
        List<CityDto> cities = cityService.getTopCitiesByDepartmentCode(departmentCode, limit);
        return ResponseEntity.ok(cities);
    }


}