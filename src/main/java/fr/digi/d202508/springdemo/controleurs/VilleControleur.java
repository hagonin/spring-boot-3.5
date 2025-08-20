package fr.digi.d202508.springdemo.controleurs;

import fr.digi.d202508.springdemo.services.VilleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/villes")
/**
 * Contrôleur REST gérant un catalogue en mémoire de villes et les opérations CRUD associées.
 */
public class VilleControleur {

    private List<VilleService> villes = new ArrayList<>(Arrays.asList(
        new VilleService(1,"Montpellier", 295542),
        new VilleService(2,"Paris", 2165423),
        new VilleService(3,"Lyon", 518635),
        new VilleService(4,"Marseille", 870018),
        new VilleService(5,"Toulouse", 493465),
        new VilleService(6,"Nantes", 318808),
        new VilleService(7,"Bordeaux", 257068),
        new VilleService(8,"Strasbourg", 284677)
    ));

    @GetMapping
    /**
     * Récupère la liste complète des villes.
     *
     * @return la liste des villes en mémoire
     */
    public List<VilleService> getVilles() {
        return villes;
    }

    @PostMapping
    /**
     * Ajoute une nouvelle ville après validation.
     *
     * @param nouvelleVille la ville à ajouter
     * @param bindingResult résultat de la validation de la requête
     * @return 200 si ajout réussi, 400 si erreurs de validation ou doublons
     */
    public ResponseEntity<String> ajouterVille(@Valid @RequestBody VilleService nouvelleVille, BindingResult bindingResult) {
        // Vérifier les erreurs de validation
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> 
                errors.append(error.getDefaultMessage()).append("; ")
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }
        
        // Vérifier si l'ID existe déjà
        for (VilleService ville : villes) {
            if (ville.getId() == nouvelleVille.getId()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Une ville avec cet ID existe déjà");
            }
        }
        
        // Vérifier si la ville existe déjà
        for (VilleService ville : villes) {
            if (ville.getNom().equalsIgnoreCase(nouvelleVille.getNom())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La ville existe déjà");
            }
        }
        
        villes.add(nouvelleVille);
        return ResponseEntity.ok("Ville succeed");
    }

    @GetMapping("/{id}")
    /**
     * Récupère une ville par son identifiant.
     *
     * @param id identifiant de la ville
     * @return 200 avec la ville si trouvée, 404 sinon
     */
    public ResponseEntity<VilleService> getVilleById(@PathVariable int id) {
        for (VilleService ville : villes) {
            if (ville.getId() == id) {
                return ResponseEntity.ok(ville);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    /**
     * Met à jour une ville existante.
     *
     * @param id identifiant de la ville à modifier
     * @param villeModifiee nouvelle représentation de la ville
     * @param bindingResult résultat de la validation
     * @return 200 si succès, 400 si erreurs de validation, 404 si la ville n'existe pas
     */
    public ResponseEntity<String> modifierVille(@PathVariable int id, @Valid @RequestBody VilleService villeModifiee, BindingResult bindingResult) {
        // Vérifier les erreurs de validation
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> 
                errors.append(error.getDefaultMessage()).append("; ")
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }
        
        for (int i = 0; i < villes.size(); i++) {
            if (villes.get(i).getId() == id) {
                villeModifiee.setId(id);
                villes.set(i, villeModifiee);
                return ResponseEntity.ok("Ville modifiée avec succès");
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    /**
     * Supprime une ville par son identifiant.
     *
     * @param id identifiant de la ville à supprimer
     * @return 200 si supprimée, 404 si introuvable
     */
    public ResponseEntity<String> supprimerVille(@PathVariable int id) {
        for (int i = 0; i < villes.size(); i++) {
            if (villes.get(i).getId() == id) {
                villes.remove(i);
                return ResponseEntity.ok("Ville supprimée avec succès");
            }
        }
        return ResponseEntity.notFound().build();
    }

}
