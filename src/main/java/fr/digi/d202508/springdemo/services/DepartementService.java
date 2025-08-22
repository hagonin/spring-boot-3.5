package fr.digi.d202508.springdemo.services;

import fr.digi.d202508.springdemo.daos.DepartementDao;
import fr.digi.d202508.springdemo.entities.Departement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service gérant la logique métier des opérations sur les départements.
 */
@Service
@Transactional(readOnly = true)
public class DepartementService {

    @Autowired
    private DepartementDao departementDao;

    /**
     * Récupère tous les départements.
     * @return la liste des départements
     */
    public List<Departement> getAllDepartments() {
        return departementDao.findAll();
    }

    /**
     * Récupère un département par son identifiant.
     * @param id identifiant du département
     * @return le département si trouvé, null sinon
     */
    public Departement getDepartmentById(Long id) {
        return departementDao.findById(id);
    }

    /**
     * Récupère un département par son code.
     * @param code code du département
     * @return le département si trouvé, null sinon
     */
    public Departement getDepartmentByCode(String code) {
        return departementDao.findByCode(code);
    }

    /**
     * Crée un nouveau département.
     * @param departement le département à créer
     * @return le département créé
     */
    @Transactional
    public Departement createDepartment(Departement departement) {
        return departementDao.save(departement);
    }

    /**
     * Met à jour un département existant.
     * @param id identifiant du département à modifier
     * @param departementModifie nouvelle représentation du département
     * @return le département modifié, ou null si non trouvé
     */
    @Transactional
    public Departement updateDepartment(Long id, Departement departementModifie) {
        if (departementDao.existsById(id)) {
            departementModifie.setId(id);
            return departementDao.save(departementModifie);
        }
        return null;
    }

    /**
     * Supprime un département par son identifiant.
     * @param id identifiant du département à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    @Transactional
    public boolean deleteDepartmentById(Long id) {
        if (departementDao.existsById(id)) {
            departementDao.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Vérifie si un département existe par son identifiant.
     * @param id identifiant du département
     * @return true si le département existe, false sinon
     */
    public boolean departmentExistsById(Long id) {
        return departementDao.existsById(id);
    }

    /**
     * Vérifie si un département existe par son code.
     * @param code code du département
     * @return true si le département existe, false sinon
     */
    public boolean departmentExistsByCode(String code) {
        return departementDao.existsByCode(code);
    }
}