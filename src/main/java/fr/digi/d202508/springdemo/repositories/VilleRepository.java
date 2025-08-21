package fr.digi.d202508.springdemo.repositories;

import fr.digi.d202508.springdemo.services.VilleService;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Dépôt en mémoire gérant la collection de villes et les opérations d'accès.
 */
@Repository
public class VilleRepository {

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

    /**
     * Retourne toutes les villes.
     * @return la liste des villes
     */
    public List<VilleService> findAll() {
        return villes;
    }

    /**
     * Recherche une ville par identifiant.
     * @param id identifiant recherché
     * @return option contenant la ville si trouvée
     */
    public Optional<VilleService> findById(int id) {
        return villes.stream()
            .filter(ville -> ville.getId() == id)
            .findFirst();
    }

    /**
     * Vérifie l'existence d'une ville par identifiant.
     * @param id identifiant de la ville
     * @return vrai si une ville possède cet identifiant
     */
    public boolean existsById(int id) {
        return villes.stream()
            .anyMatch(ville -> ville.getId() == id);
    }

    /**
     * Vérifie l'existence d'une ville par nom (insensible à la casse).
     * @param nom nom de la ville
     * @return vrai si une ville possède ce nom
     */
    public boolean existsByNom(String nom) {
        return villes.stream()
            .anyMatch(ville -> ville.getNom().equalsIgnoreCase(nom));
    }

    /**
     * Ajoute une nouvelle ville au dépôt.
     * @param ville la ville à ajouter
     */
    public void save(VilleService ville) {
        villes.add(ville);
    }

    /**
     * Met à jour une ville existante par identifiant.
     * @param id identifiant de la ville à modifier
     * @param villeModifiee nouvelle représentation de la ville
     * @return vrai si la mise à jour a réussi
     */
    public boolean update(int id, VilleService villeModifiee) {
        for (int i = 0; i < villes.size(); i++) {
            if (villes.get(i).getId() == id) {
                villeModifiee.setId(id);
                villes.set(i, villeModifiee);
                return true;
            }
        }
        return false;
    }

    /**
     * Supprime une ville par identifiant.
     * @param id identifiant à supprimer
     * @return vrai si la suppression a eu lieu
     */
    public boolean deleteById(int id) {
        Iterator<VilleService> iterator = villes.iterator();
        while (iterator.hasNext()) {
            VilleService ville = iterator.next();
            if (ville.getId() == id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
}