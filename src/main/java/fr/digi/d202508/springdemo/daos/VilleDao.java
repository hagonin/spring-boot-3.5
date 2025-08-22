package fr.digi.d202508.springdemo.daos;

import fr.digi.d202508.springdemo.entities.Ville;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DAO gérant l'accès aux données des villes via EntityManager
 */
@Repository
@Transactional
public class VilleDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Récupère toutes les villes
     * @return la liste des villes
     */
    public List<Ville> findAll() {
        TypedQuery<Ville> query = entityManager.createQuery("SELECT v FROM Ville v", Ville.class);
        return query.getResultList();
    }

    /**
     * Récupère une ville par son identifiant
     * @param id identifiant de la ville
     * @return la ville si trouvée, null sinon
     */
    public Ville findById(Long id) {
        return entityManager.find(Ville.class, id);
    }

    /**
     * Récupère une ville par son nom
     * @param nom nom de la ville
     * @return la ville si trouvée, null sinon
     */
    public Ville findByName(String nom) {
        TypedQuery<Ville> query = entityManager.createQuery(
            "SELECT v FROM Ville v WHERE LOWER(v.nom) = LOWER(:nom)", Ville.class);
        query.setParameter("nom", nom);
        List<Ville> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Vérifie si une ville existe par son nom
     * @param nom nom de la ville
     * @return true si la ville existe, false sinon
     */
    public boolean existsByNom(String nom) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(v) FROM Ville v WHERE LOWER(v.nom) = LOWER(:nom)", Long.class);
        query.setParameter("nom", nom);
        return query.getSingleResult() > 0;
    }

    /**
     * Vérifie si une ville existe par son identifiant
     * @param id identifiant de la ville
     * @return true si la ville existe, false sinon
     */
    public boolean existsById(Long id) {
        return findById(id) != null;
    }

    /**
     * Sauvegarde ou met à jour une ville
     * @param ville la ville à sauvegarder
     * @return la ville sauvegardée
     */
    public Ville save(Ville ville) {
        if (ville.getId() == null) {
            entityManager.persist(ville);
            return ville;
        } else {
            return entityManager.merge(ville);
        }
    }

    /**
     * Supprime une ville par son identifiant
     * @param id identifiant de la ville à supprimer
     */
    public void deleteById(Long id) {
        Ville ville = findById(id);
        if (ville != null) {
            entityManager.remove(ville);
        }
    }

    /**
     * Récupère les N villes les plus peuplées d'un département
     * @param departementCode code du département
     * @param n nombre de villes à récupérer
     * @return la liste des villes triées par population décroissante
     */
    public List<Ville> findTopCitiesByDepartementCode(String departementCode, int n) {
        TypedQuery<Ville> query = entityManager.createQuery(
            "SELECT v FROM Ville v WHERE v.departement.code = :code ORDER BY v.population DESC", Ville.class);
        query.setParameter("code", departementCode);
        query.setMaxResults(n);
        return query.getResultList();
    }

    /**
     * Récupère les villes d'un département dans une tranche de population
     * @param departementCode code du département
     * @param minPopulation population minimale
     * @param maxPopulation population maximale
     * @return la liste des villes dans la tranche de population
     */
    public List<Ville> findCitiesByDepartementCodeAndPopulationRange(String departementCode, int minPopulation, int maxPopulation) {
        TypedQuery<Ville> query = entityManager.createQuery(
            "SELECT v FROM Ville v WHERE v.departement.code = :code AND v.population >= :min AND v.population <= :max ORDER BY v.population DESC", Ville.class);
        query.setParameter("code", departementCode);
        query.setParameter("min", minPopulation);
        query.setParameter("max", maxPopulation);
        return query.getResultList();
    }
}