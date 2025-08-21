package fr.digi.d202508.springdemo.controleurs;

import fr.digi.d202508.springdemo.repositories.VilleRepository;
import fr.digi.d202508.springdemo.services.VilleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST gérant les opérations CRUD des villes via un dépôt en mémoire.
 */
@RestController
@RequestMapping("/villes")
public class VilleControleur {

    @Autowired
    private VilleRepository villeRepository;

    /**
     * Valide la requête et retourne une réponse d'erreur le cas échéant.
     * @param bindingResult résultat de la validation
     * @return une réponse 400 si erreurs, sinon null
     */
    private ResponseEntity<String> validerVille(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> 
                errors.append(error.getDefaultMessage()).append("; ")
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }
        return null;
    }

    /**
     * Récupère la liste complète des villes.
     * @return la liste des villes
     */
    @GetMapping
    public List<VilleService> getVilles() {
        return villeRepository.findAll();
    }

    /**
     * Ajoute une nouvelle ville après validation.
     * @param nouvelleVille la ville à ajouter
     * @param bindingResult résultat de la validation de la requête
     * @return 200 si ajout réussi, 400 si erreurs de validation ou doublons
     */
    @PostMapping
    public ResponseEntity<String> ajouterVille(@Valid @RequestBody VilleService nouvelleVille, BindingResult bindingResult) {
        ResponseEntity<String> validationError = validerVille(bindingResult);
        if (validationError != null) {
            return validationError;
        }
        
        if (villeRepository.existsById(nouvelleVille.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Une ville avec cet ID existe déjà");
        }
        
        if (villeRepository.existsByNom(nouvelleVille.getNom())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La ville existe déjà");
        }
        
        villeRepository.save(nouvelleVille);
        return ResponseEntity.ok("Ville succeed");
    }

    /**
     * Récupère une ville par son identifiant.
     * @param id identifiant de la ville
     * @return 200 avec la ville si trouvée, 404 sinon
     */
    @GetMapping("/{id}")
    public ResponseEntity<VilleService> getVilleById(@PathVariable int id) {
        Optional<VilleService> ville = villeRepository.findById(id);
        return ville.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Met à jour une ville existante.
     * @param id identifiant de la ville à modifier
     * @param villeModifiee nouvelle représentation de la ville
     * @param bindingResult résultat de la validation
     * @return 200 si succès, 400 si erreurs de validation, 404 si la ville n'existe pas
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> modifierVille(@PathVariable int id, @Valid @RequestBody VilleService villeModifiee, BindingResult bindingResult) {
        ResponseEntity<String> validationError = validerVille(bindingResult);
        if (validationError != null) {
            return validationError;
        }
        
        if (villeRepository.update(id, villeModifiee)) {
            return ResponseEntity.ok("Ville modifiée avec succès");
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Supprime une ville par son identifiant.
     * @param id identifiant de la ville à supprimer
     * @return 200 si supprimée, 404 si introuvable
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimerVille(@PathVariable int id) {
        if (villeRepository.deleteById(id)) {
            return ResponseEntity.ok("Ville supprimée avec succès");
        }
        return ResponseEntity.notFound().build();
    }

}
