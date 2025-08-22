package fr.digi.d202508.springdemo.daos;

import fr.digi.d202508.springdemo.entities.Departement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DAO gérant l'accès aux données des départements via EntityManager.
 */
@Repository
@Transactional
public class DepartementDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Récupère tous les départements.
     * @return la liste des départements
     */
    public List<Departement> findAll() {
        TypedQuery<Departement> query = entityManager.createQuery("SELECT d FROM Departement d", Departement.class);
        return query.getResultList();
    }

    /**
     * Récupère un département par son identifiant.
     * @param id identifiant du département
     * @return le département si trouvé, null sinon
     */
    public Departement findById(Long id) {
        return entityManager.find(Departement.class, id);
    }

    /**
     * Récupère un département par son code.
     * @param code code du département
     * @return le département si trouvé, null sinon
     */
    public Departement findByCode(String code) {
        TypedQuery<Departement> query = entityManager.createQuery(
            "SELECT d FROM Departement d WHERE d.code = :code", Departement.class);
        query.setParameter("code", code);
        List<Departement> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Vérifie si un département existe par son identifiant.
     * @param id identifiant du département
     * @return true si le département existe, false sinon
     */
    public boolean existsById(Long id) {
        return findById(id) != null;
    }

    /**
     * Vérifie si un département existe par son code.
     * @param code code du département
     * @return true si le département existe, false sinon
     */
    public boolean existsByCode(String code) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(d) FROM Departement d WHERE d.code = :code", Long.class);
        query.setParameter("code", code);
        return query.getSingleResult() > 0;
    }

    /**
     * Sauvegarde ou met à jour un département.
     * @param departement le département à sauvegarder
     * @return le département sauvegardé
     */
    public Departement save(Departement departement) {
        if (departement.getId() == null) {
            entityManager.persist(departement);
            return departement;
        } else {
            return entityManager.merge(departement);
        }
    }

    /**
     * Supprime un département par son identifiant.
     * @param id identifiant du département à supprimer
     */
    public void deleteById(Long id) {
        Departement departement = findById(id);
        if (departement != null) {
            entityManager.remove(departement);
        }
    }
}