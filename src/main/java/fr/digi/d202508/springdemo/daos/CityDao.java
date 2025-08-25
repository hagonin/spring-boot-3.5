package fr.digi.d202508.springdemo.daos;

import fr.digi.d202508.springdemo.entities.City;
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
public class CityDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Récupère toutes les villes
     * @return la liste des villes
     */
    public List<City> findAll() {
        TypedQuery<City> query = entityManager.createQuery("SELECT v FROM City v", City.class);
        return query.getResultList();
    }

    /**
     * Récupère une ville par son identifiant
     * @param id identifiant de la ville
     * @return la ville si trouvée, null sinon
     */
    public City findById(Long id) {
        return entityManager.find(City.class, id);
    }

    /**
     * Récupère une ville par son nom
     * @param nom nom de la ville
     * @return la ville si trouvée, null sinon
     */
    public City findByName(String name) {
        TypedQuery<City> query = entityManager.createQuery(
            "SELECT v FROM City v WHERE LOWER(v.name) = LOWER(:name)", City.class);
        query.setParameter("name", name);
        List<City> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Vérifie si une ville existe par son nom
     * @param nom nom de la ville
     * @return true si la ville existe, false sinon
     */
    public boolean existsByName(String name) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(v) FROM City v WHERE LOWER(v.name) = LOWER(:name)", Long.class);
        query.setParameter("name", name);
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
    public City save(City city) {
        if (city.getId() == null) {
            entityManager.persist(city);
            return city;
        } else {
            return entityManager.merge(city);
        }
    }

    /**
     * Supprime une ville par son identifiant
     * @param id identifiant de la ville à supprimer
     */
    public void deleteById(Long id) {
        City city = findById(id);
        if (city != null) {
            entityManager.remove(city);
        }
    }

    /**
     * Récupère les N villes les plus peuplées d'un département
     * @param departementCode code du département
     * @param n nombre de villes à récupérer
     * @return la liste des villes triées par population décroissante
     */
    public List<City> findTopCitiesByDepartmentCode(String departmentCode, int n) {
        TypedQuery<City> query = entityManager.createQuery(
            "SELECT v FROM City v WHERE v.department.code = :code ORDER BY v.population DESC", City.class);
        query.setParameter("code", departmentCode);
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
    public List<City> findCitiesByDepartmentCodeAndPopulationRange(String departmentCode, int minPopulation, int maxPopulation) {
        TypedQuery<City> query = entityManager.createQuery(
            "SELECT v FROM City v WHERE v.department.code = :code AND v.population >= :min AND v.population <= :max ORDER BY v.population DESC", City.class);
        query.setParameter("code", departmentCode);
        query.setParameter("min", minPopulation);
        query.setParameter("max", maxPopulation);
        return query.getResultList();
    }
}