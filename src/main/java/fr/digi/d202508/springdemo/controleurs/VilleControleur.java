package fr.digi.d202508.springdemo.controleurs;

import fr.digi.d202508.springdemo.services.VilleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/villes")
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
    public List<VilleService> getVilles() {
        return villes;
    }

    @PostMapping
    public ResponseEntity<String> ajouterVille(@RequestBody VilleService nouvelleVille) {
        for (VilleService ville : villes) {
            if (ville.getNom().equalsIgnoreCase(nouvelleVille.getNom())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La ville existe déjà");
            }
        }
        
        villes.add(nouvelleVille);
        return ResponseEntity.ok("Ville succeed");
    }

    @GetMapping("/{id}")
    public ResponseEntity<VilleService> getVilleById(@PathVariable int id) {
        for (VilleService ville : villes) {
            if (ville.getId() == id) {
                return ResponseEntity.ok(ville);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> modifierVille(@PathVariable int id, @RequestBody VilleService villeModifiee) {
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
