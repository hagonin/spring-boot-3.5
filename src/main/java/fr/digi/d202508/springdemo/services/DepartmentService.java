package fr.digi.d202508.springdemo.services;

import fr.digi.d202508.springdemo.daos.DepartmentDao;
import fr.digi.d202508.springdemo.dtos.DepartmentDto;
import fr.digi.d202508.springdemo.entities.Department;
import fr.digi.d202508.springdemo.mappers.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service gérant la logique métier des opérations sur les départements.
 */
@Service
@Transactional(readOnly = true)
public class DepartmentService {

    @Autowired
    private DepartmentDao departementDao;
    
    @Autowired
    private DepartmentMapper departementMapper;

    /**
     * Récupère tous les départements.
     * @return la liste des DTOs des départements
     */
    public List<DepartmentDto> getAllDepartments() {
        List<Department> departements = departementDao.findAll();
        return departementMapper.toDtoList(departements);
    }

    /**
     * Récupère un département par son identifiant.
     * @param id identifiant du département
     * @return le DTO du département si trouvé, null sinon
     */
    public DepartmentDto getDepartmentById(Long id) {
        Department departement = departementDao.findById(id);
        return departementMapper.toDto(departement);
    }

    /**
     * Récupère un département par son code.
     * @param code code du département
     * @return le DTO du département si trouvé, null sinon
     */
    public DepartmentDto getDepartmentByCode(String code) {
        Department departement = departementDao.findByCode(code);
        return departementMapper.toDto(departement);
    }

    /**
     * Crée un nouveau département.
     * @param departementDto le DTO du département à créer
     * @return le DTO du département créé
     */
    @Transactional
    public DepartmentDto createDepartment(DepartmentDto departementDto) {
        Department departement = departementMapper.toEntity(departementDto);
        Department departementSauve = departementDao.save(departement);
        return departementMapper.toDto(departementSauve);
    }

    /**
     * Met à jour un département existant.
     * @param id identifiant du département à modifier
     * @param departementDto nouvelle représentation du département
     * @return le DTO du département modifié, ou null si non trouvé
     */
    @Transactional
    public DepartmentDto updateDepartment(Long id, DepartmentDto departementDto) {
        Department departementExistant = departementDao.findById(id);
        if (departementExistant == null) {
            return null;
        }
        
        departementMapper.updateEntityFromDto(departementDto, departementExistant);
        Department departementMisAJour = departementDao.save(departementExistant);
        return departementMapper.toDto(departementMisAJour);
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