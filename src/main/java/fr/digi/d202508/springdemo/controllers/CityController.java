package fr.digi.d202508.springdemo.controllers;

import fr.digi.d202508.springdemo.dtos.CityDto;
import fr.digi.d202508.springdemo.dtos.DepartmentDto;
import fr.digi.d202508.springdemo.exceptions.ApplicationException;
import fr.digi.d202508.springdemo.services.CityService;
import fr.digi.d202508.springdemo.services.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/cities")
@Tag(name = "Villes", description = "API pour la gestion des villes françaises avec recherches avancées")
public class CityController {

    @Autowired
    private CityService cityService;
    
    @Autowired
    private DepartmentService departmentService;
    
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
    @Operation(summary = "Retourne la liste de toutes les villes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "Liste des villes au format JSON",
                content = {@Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = CityDto.class)))})
    })
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
    public ResponseEntity<CityDto> getCityById(@PathVariable int id) throws ApplicationException {
        CityDto city = cityService.getCityById(id);
        return ResponseEntity.ok(city);
    }

    /**
     * Récupère une ville par son nom
     * @param name nom de la ville
     * @return 200 avec la ville si trouvée, 404 sinon
     */
    @GetMapping("/city-name/{name}")
    public ResponseEntity<CityDto> getCityByName(@PathVariable String name) throws ApplicationException {
        CityDto city = cityService.getCityByName(name);
        return ResponseEntity.ok(city);
    }

    /**
     * Ajoute une nouvelle ville après validation
     * @param newCity la ville à ajouter
     * @param result résultat de la validation de la requête
     * @return 201 si ajout réussi, 400 si erreurs de validation ou doublons
     */
    @PostMapping
    public ResponseEntity<?> createCity(@Valid @RequestBody CityDto newCity, BindingResult result) throws ApplicationException {
        ResponseEntity<String> validationError = validateCity(result);
        if (validationError != null) {
            return validationError;
        }
        
        CityDto createdCity = cityService.createCity(newCity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCity);
    }

    /**
     * Met à jour une ville existante
     * @param id identifiant de la ville à modifier
     * @param updatedCity nouvelle représentation de la ville
     * @param result résultat de la validation
     * @return 200 si succès, 400 si erreurs de validation, 404 si la ville n'existe pas
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCity(@PathVariable int id, @Valid @RequestBody CityDto updatedCity, BindingResult result) throws ApplicationException {
        ResponseEntity<String> validationError = validateCity(result);
        if (validationError != null) {
            return validationError;
        }
        
        CityDto updatedCityDto = cityService.updateCity(id, updatedCity);
        return ResponseEntity.ok(updatedCityDto);
    }

    /**
     * Supprime une ville par son identifiant
     * @param id identifiant de la ville à supprimer
     * @return 200 si supprimée, 404 si introuvable
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCity(@PathVariable int id) throws ApplicationException {
        cityService.deleteCity(id);
        return ResponseEntity.ok(messageSource.getMessage("ville.deleted", null, LocaleContextHolder.getLocale()));
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
            @RequestParam int n) throws ApplicationException {
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
            @RequestParam int max) throws ApplicationException {
        List<CityDto> cities = cityService.getCitiesByDepartmentAndPopulationRange(departmentCode, min, max);
        return ResponseEntity.ok(cities);
    }

    // === NOUVELLES ROUTES UTILISANT VILLEREPOSITORY ===

    /**
     * Recherche toutes les villes dont le nom commence par une chaîne donnée
     * @param prefix le préfixe du nom de la ville
     * @return la liste des villes correspondantes
     */
    @Operation(summary = "Recherche des villes par préfixe de nom",
               description = "Retourne toutes les villes dont le nom commence par la chaîne spécifiée")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "Liste des villes trouvées",
                content = {@Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = CityDto.class)))}),
        @ApiResponse(responseCode = "400",
                description = "Préfixe invalide ou aucune ville trouvée",
                content = @Content())
    })
    @GetMapping("/starts-with/{prefix}")
    public ResponseEntity<List<CityDto>> getCitiesStartingWith(
            @Parameter(description = "Préfixe du nom de ville (minimum 2 caractères)", example = "Par", required = true)
            @PathVariable String prefix) throws ApplicationException {
        List<CityDto> cities = cityService.getCitiesStartingWith(prefix);
        return ResponseEntity.ok(cities);
    }

    /**
     * Recherche toutes les villes dont la population est supérieure à un minimum
     * @param minPopulation la population minimum
     * @return la liste des villes triées par population décroissante
     */
    @GetMapping("/population-min")
    public ResponseEntity<List<CityDto>> getCitiesWithMinPopulation(@RequestParam int minPopulation) throws ApplicationException {
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
            @RequestParam int maxPopulation) throws ApplicationException {
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
            @RequestParam int minPopulation) throws ApplicationException {
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
    @GetMapping("/department/{departmentId}/population-between")
    public ResponseEntity<List<CityDto>> getCitiesByDepartmentAndPopulationBetween(
            @PathVariable Long departmentId,
            @RequestParam int minPopulation,
            @RequestParam int maxPopulation) throws ApplicationException {
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
            @RequestParam int limit) throws ApplicationException {
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
            @RequestParam int minPopulation) throws ApplicationException {
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
            @RequestParam int maxPopulation) throws ApplicationException {
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
            @RequestParam int limit) throws ApplicationException {
        List<CityDto> cities = cityService.getTopCitiesByDepartmentCode(departmentCode, limit);
        return ResponseEntity.ok(cities);
    }

    /**
     * Exporte en CSV toutes les villes dont la population est supérieure à un minimum donné
     * @param minPopulation la population minimum
     * @return fichier CSV avec nom de ville, nombre d'habitants, code département, nom département
     */
    @Operation(summary = "Exporte les villes en CSV par population minimum",
               description = "Retourne un fichier CSV contenant les villes avec population supérieure au minimum spécifié")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "Fichier CSV généré avec succès",
                content = {@Content(mediaType = "text/csv")}),
        @ApiResponse(responseCode = "400",
                description = "Population minimum invalide",
                content = @Content())
    })
    @GetMapping("/export-city/csv")
    public ResponseEntity<byte[]> exportCitiesWithMinPopulationToCsv(
            @Parameter(description = "Population minimum pour l'export", example = "50000", required = true)
            @RequestParam int minPopulation) throws ApplicationException {
        
        if (minPopulation < 0) {
            throw new ApplicationException(messageSource.getMessage("ville.export.csv.population.negative", null, LocaleContextHolder.getLocale()));
        }
        
        List<CityDto> cities = cityService.getCitiesWithMinPopulation(minPopulation);
        
        try {
            byte[] csvContent = generateCsvContent(cities);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "cities_population_min_" + minPopulation + ".csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvContent);
                    
        } catch (IOException e) {
            throw new ApplicationException(messageSource.getMessage("ville.export.csv.generation.error", new Object[]{e.getMessage()}, LocaleContextHolder.getLocale()));
        }
    }
    
    /**
     * Génère le contenu CSV pour les villes
     * @param cities la liste des villes
     * @return le contenu CSV sous forme de bytes
     * @throws IOException si erreur d'écriture
     * @throws ApplicationException si erreur de récupération des départements
     */
    private byte[] generateCsvContent(List<CityDto> cities) throws IOException, ApplicationException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        
        // En-tête CSV
        writer.write("Nom de la ville,Nombre d'habitants,Code département,Nom du département\n");
        
        // Données des villes
        for (CityDto city : cities) {
            String departmentName = "";
            try {
                DepartmentDto department = departmentService.getDepartmentByCode(city.getDepartmentCode());
                departmentName = department.getName();
                // Si le nom du département est vide ou null, utiliser le code
                if (departmentName == null || departmentName.trim().isEmpty()) {
                    departmentName = "Département " + city.getDepartmentCode();
                }
            } catch (ApplicationException e) {
                departmentName = messageSource.getMessage("ville.export.csv.department.unknown", null, LocaleContextHolder.getLocale());
            }
            
            writer.write(String.format("\"%s\",%d,\"%s\",\"%s\"\n",
                    escapeCsv(city.getName()),
                    city.getPopulation(),
                    escapeCsv(city.getDepartmentCode()),
                    escapeCsv(departmentName)));
        }
        
        writer.flush();
        writer.close();
        
        return outputStream.toByteArray();
    }
    
    /**
     * Échappe les caractères spéciaux pour le CSV
     * @param value la valeur à échapper
     * @return la valeur échappée
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"");
    }


}