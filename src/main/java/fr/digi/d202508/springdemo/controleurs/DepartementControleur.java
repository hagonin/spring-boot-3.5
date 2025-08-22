package fr.digi.d202508.springdemo.controleurs;

import fr.digi.d202508.springdemo.entities.Departement;
import fr.digi.d202508.springdemo.services.DepartementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Contrôleur REST gérant les opérations CRUD des départements.
 */
@RestController
@RequestMapping("/departements")
public class DepartementControleur {

    @Autowired
    private DepartementService departementService;

    /**
     * Valide la requête et retourne une réponse d'erreur le cas échéant.
     * @param result résultat de la validation
     * @return une réponse 400 si erreurs, sinon null
     */
    private ResponseEntity<String> validerDepartement(BindingResult result) {
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
     * Récupère la liste complète des départements.
     * @return la liste des départements
     */
    @GetMapping
    public List<Departement> getDepartements() {
        return departementService.findAll();
    }

    /**
     * Récupère un département par son identifiant.
     * @param id identifiant du département
     * @return 200 avec le département si trouvé, 404 sinon
     */
    @GetMapping("/{id}")
    public ResponseEntity<Departement> getDepartementById(@PathVariable Long id) {
        Departement departement = departementService.findById(id);
        if (departement != null) {
            return ResponseEntity.ok(departement);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Récupère un département par son code.
     * @param code code du département
     * @return 200 avec le département si trouvé, 404 sinon
     */
    @GetMapping("/code-departement/{code}")
    public ResponseEntity<Departement> getDepartementByCode(@PathVariable String code) {
        Departement departement = departementService.findByCode(code);
        if (departement != null) {
            return ResponseEntity.ok(departement);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Ajoute un nouveau département après validation.
     * @param nouveauDepartement le département à ajouter
     * @param result résultat de la validation de la requête
     * @return 201 si ajout réussi, 400 si erreurs de validation ou doublons
     */
    @PostMapping
    public ResponseEntity<?> ajouterDepartement(@Valid @RequestBody Departement nouveauDepartement, BindingResult result) {
        ResponseEntity<String> validationError = validerDepartement(result);
        if (validationError != null) {
            return validationError;
        }
        
        // Vérifier si le département existe déjà par code
        if (departementService.existsByCode(nouveauDepartement.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Un département avec ce code existe déjà");
        }
        
        Departement departementCree = departementService.create(nouveauDepartement);
        return ResponseEntity.status(HttpStatus.CREATED).body(departementCree);
    }

    /**
     * Met à jour un département existant.
     * @param id identifiant du département à modifier
     * @param departementModifie nouvelle représentation du département
     * @param result résultat de la validation
     * @return 200 si succès, 400 si erreurs de validation, 404 si le département n'existe pas
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> modifierDepartement(@PathVariable Long id, @Valid @RequestBody Departement departementModifie, BindingResult result) {
        ResponseEntity<String> validationError = validerDepartement(result);
        if (validationError != null) {
            return validationError;
        }
        
        Departement departementMisAJour = departementService.update(id, departementModifie);
        if (departementMisAJour != null) {
            return ResponseEntity.ok(departementMisAJour);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Supprime un département par son identifiant.
     * @param id identifiant du département à supprimer
     * @return 200 si supprimé, 404 si introuvable
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimerDepartement(@PathVariable Long id) {
        if (departementService.deleteById(id)) {
            return ResponseEntity.ok("Département supprimé avec succès");
        }
        return ResponseEntity.notFound().build();
    }
}