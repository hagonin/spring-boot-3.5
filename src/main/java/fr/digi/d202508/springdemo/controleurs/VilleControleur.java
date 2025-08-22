package fr.digi.d202508.springdemo.controleurs;

import fr.digi.d202508.springdemo.entities.Ville;
import fr.digi.d202508.springdemo.services.VilleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Contrôleur REST gérant les opérations CRUD des villes via un service métier
 */
@RestController
@RequestMapping("/villes")
public class VilleControleur {

    @Autowired
    private VilleService villeService;
    
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
    public List<Ville> getCities() {
        return villeService.getAllCities();
    }

    /**
     * Récupère une ville par son identifiant.
     * @param id identifiant de la ville
     * @return 200 avec la ville si trouvée, 404 sinon
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ville> getCityById(@PathVariable int id) {
        Ville ville = villeService.getCityById(id);
        if (ville != null) {
            return ResponseEntity.ok(ville);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Récupère une ville par son nom.
     * @param nom nom de la ville
     * @return 200 avec la ville si trouvée, 404 sinon
     */
    @GetMapping("/nom-ville/{nom}")
    public ResponseEntity<Ville> getCityByName(@PathVariable String nom) {
        Ville ville = villeService.getCityByName(nom);
        if (ville != null) {
            return ResponseEntity.ok(ville);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Ajoute une nouvelle ville après validation.
     * @param nouvelleVille la ville à ajouter
     * @param result résultat de la validation de la requête
     * @return 201 si ajout réussi, 400 si erreurs de validation ou doublons
     */
    @PostMapping
    public ResponseEntity<?> createCity(@Valid @RequestBody Ville nouvelleVille, BindingResult result) {
        ResponseEntity<String> validationError = validateCity(result);
        if (validationError != null) {
            return validationError;
        }
        
        // Vérifier si la ville existe déjà par nom
        if (villeService.getCityByName(nouvelleVille.getNom()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(messageSource.getMessage("ville.nom.exists", null, LocaleContextHolder.getLocale()));
        }
        
        List<Ville> villes = villeService.createCity(nouvelleVille);
        return ResponseEntity.status(HttpStatus.CREATED).body(villes);
    }

    /**
     * Met à jour une ville existante.
     * @param id identifiant de la ville à modifier
     * @param villeModifiee nouvelle représentation de la ville
     * @param result résultat de la validation
     * @return 200 si succès, 400 si erreurs de validation, 404 si la ville n'existe pas
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCity(@PathVariable int id, @Valid @RequestBody Ville villeModifiee, BindingResult result) {
        ResponseEntity<String> validationError = validateCity(result);
        if (validationError != null) {
            return validationError;
        }
        
        if (villeService.getCityById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        List<Ville> villes = villeService.updateCity(id, villeModifiee);
        return ResponseEntity.ok(villes);
    }

    /**
     * Supprime une ville par son identifiant.
     * @param id identifiant de la ville à supprimer
     * @return 200 si supprimée, 404 si introuvable
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable int id) {
        if (villeService.getCityById(id) != null) {
            List<Ville> villes = villeService.deleteCity(id);
            return ResponseEntity.ok(villes);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Récupère les N villes les plus peuplées d'un département.
     * @param departementCode code du département
     * @param n nombre de villes à récupérer
     * @return la liste des villes triées par population décroissante
     */
    @GetMapping("/top")
    public ResponseEntity<List<Ville>> getTopCitiesByDepartment(
            @RequestParam String departementCode, 
            @RequestParam int n) {
        List<Ville> villes = villeService.getTopCitiesByDepartment(departementCode, n);
        return ResponseEntity.ok(villes);
    }

    /**
     * Récupère les villes d'un département dans une tranche de population.
     * @param departementCode code du département
     * @param min population minimale
     * @param max population maximale
     * @return la liste des villes dans la tranche de population
     */
    @GetMapping("/by-population")
    public ResponseEntity<List<Ville>> getCitiesByPopulationRange(
            @RequestParam String departementCode,
            @RequestParam int min,
            @RequestParam int max) {
        List<Ville> villes = villeService.getCitiesByDepartmentAndPopulationRange(departementCode, min, max);
        return ResponseEntity.ok(villes);
    }
}