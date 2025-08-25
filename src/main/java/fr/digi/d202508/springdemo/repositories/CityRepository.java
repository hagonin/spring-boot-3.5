package fr.digi.d202508.springdemo.repositories;

import fr.digi.d202508.springdemo.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface pour l'entité Ville avec des méthodes de recherche spécialisées.
 */
@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    
    /**
     * Recherche une ville par son nom exact.
     * @param nom le nom de la ville
     * @return la ville trouvée ou Optional.empty()
     */
    Optional<City> findByName(String nom);
    
    /**
     * Recherche toutes les villes dont le nom commence par une chaîne donnée.
     * @param prefix le préfixe du nom de la ville
     * @return la liste des villes correspondantes
     */
    List<City> findByNameStartingWithIgnoreCaseOrderByNameAsc(String prefix);
    
    /**
     * Recherche toutes les villes dont la population est supérieure à un minimum.
     * Les résultats sont triés par population décroissante.
     * @param minPopulation la population minimum
     * @return la liste des villes correspondantes
     */
    List<City> findByPopulationGreaterThanOrderByPopulationDesc(int minPopulation);
    
    /**
     * Recherche toutes les villes dont la population est comprise entre min et max.
     * Les résultats sont triés par population décroissante.
     * @param minPopulation la population minimum (inclusive)
     * @param maxPopulation la population maximum (inclusive)
     * @return la liste des villes correspondantes
     */
    List<City> findByPopulationBetweenOrderByPopulationDesc(int minPopulation, int maxPopulation);
    
    /**
     * Recherche toutes les villes d'un département dont la population est supérieure à un minimum.
     * Les résultats sont triés par population décroissante.
     * @param departementId l'ID du département
     * @param minPopulation la population minimum
     * @return la liste des villes correspondantes
     */
    @Query("SELECT v FROM City v WHERE v.department.id = :departementId AND v.population > :minPopulation ORDER BY v.population DESC")
    List<City> findByDepartementAndPopulationGreaterThan(@Param("departementId") Long departementId, @Param("minPopulation") int minPopulation);
    
    /**
     * Recherche toutes les villes d'un département dont la population est comprise entre min et max.
     * Les résultats sont triés par population décroissante.
     * @param departementId l'ID du département
     * @param minPopulation la population minimum (inclusive)
     * @param maxPopulation la population maximum (inclusive)
     * @return la liste des villes correspondantes
     */
    @Query("SELECT v FROM City v WHERE v.department.id = :departementId AND v.population BETWEEN :minPopulation AND :maxPopulation ORDER BY v.population DESC")
    List<City> findByDepartementAndPopulationBetween(@Param("departementId") Long departementId, @Param("minPopulation") int minPopulation, @Param("maxPopulation") int maxPopulation);
    
    /**
     * Recherche les n villes les plus peuplées d'un département donné.
     * @param departementId l'ID du département
     * @param limit le nombre maximum de villes à retourner
     * @return la liste des villes correspondantes
     */
    @Query("SELECT v FROM City v WHERE v.department.id = :departementId ORDER BY v.population DESC LIMIT :limit")
    List<City> findTopNByDepartementOrderByPopulationDesc(@Param("departementId") Long departementId, @Param("limit") int limit);
    
    /**
     * Recherche par code de département pour faciliter les recherches.
     * @param departementCode le code du département
     * @param minPopulation la population minimum
     * @return la liste des villes correspondantes
     */
    @Query("SELECT v FROM City v WHERE v.department.code = :departementCode AND v.population > :minPopulation ORDER BY v.population DESC")
    List<City> findByDepartementCodeAndPopulationGreaterThan(@Param("departementCode") String departementCode, @Param("minPopulation") int minPopulation);
    
    /**
     * Recherche par code de département avec population entre min et max.
     * @param departementCode le code du département
     * @param minPopulation la population minimum (inclusive)
     * @param maxPopulation la population maximum (inclusive)
     * @return la liste des villes correspondantes
     */
    @Query("SELECT v FROM City v WHERE v.department.code = :departementCode AND v.population BETWEEN :minPopulation AND :maxPopulation ORDER BY v.population DESC")
    List<City> findByDepartementCodeAndPopulationBetween(@Param("departementCode") String departementCode, @Param("minPopulation") int minPopulation, @Param("maxPopulation") int maxPopulation);
    
    /**
     * Recherche les n villes les plus peuplées d'un département par son code.
     * @param departementCode le code du département
     * @param limit le nombre maximum de villes à retourner
     * @return la liste des villes correspondantes
     */
    @Query("SELECT v FROM City v WHERE v.department.code = :departementCode ORDER BY v.population DESC LIMIT :limit")
    List<City> findTopNByDepartementCodeOrderByPopulationDesc(@Param("departementCode") String departementCode, @Param("limit") int limit);
}