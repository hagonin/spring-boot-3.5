package fr.digi.d202508.springdemo.daos;

import fr.digi.d202508.springdemo.entities.Department;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class DepartmentDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Récupère tous les départements
     * @return la liste des départements
     */
    public List<Department> findAll() {
        TypedQuery<Department> query = entityManager.createQuery("SELECT d FROM Department d", Department.class);
        return query.getResultList();
    }

    /**
     * Récupère un département par son identifiant
     * @param id identifiant du département
     * @return le département si trouvé, null sinon
     */
    public Department findById(Long id) {
        return entityManager.find(Department.class, id);
    }

    /**
     * Récupère un département par son code
     * @param code code du département
     * @return le département si trouvé, null sinon
     */
    public Department findByCode(String code) {
        TypedQuery<Department> query = entityManager.createQuery(
            "SELECT d FROM Department d WHERE d.code = :code", Department.class);
        query.setParameter("code", code);
        List<Department> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Vérifie si un département existe par son identifiant
     * @param id identifiant du département
     * @return true si le département existe, false sinon
     */
    public boolean existsById(Long id) {
        return findById(id) != null;
    }

    /**
     * Vérifie si un département existe par son code
     * @param code code du département
     * @return true si le département existe, false sinon
     */
    public boolean existsByCode(String code) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(d) FROM Department d WHERE d.code = :code", Long.class);
        query.setParameter("code", code);
        return query.getSingleResult() > 0;
    }

    /**
     * Sauvegarde ou met à jour un département
     * @param department le département à sauvegarder
     * @return le département sauvegardé
     */
    public Department save(Department department) {
        if (department.getId() == null) {
            entityManager.persist(department);
            return department;
        } else {
            return entityManager.merge(department);
        }
    }

    /**
     * Supprime un département par son identifiant
     * @param id identifiant du département à supprimer
     */
    public void deleteById(Long id) {
        Department department = findById(id);
        if (department != null) {
            entityManager.remove(department);
        }
    }
}