package fr.digi.d202508.springdemo.services;

import fr.digi.d202508.springdemo.daos.DepartmentDao;
import fr.digi.d202508.springdemo.dtos.DepartmentDto;
import fr.digi.d202508.springdemo.entities.Department;
import fr.digi.d202508.springdemo.mappers.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class DepartmentService {

    @Autowired
    private DepartmentDao departmentDao;
    
    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * Récupère tous les départements.
     * @return la liste des DTOs des départements
     */
    public List<DepartmentDto> getAllDepartments() {
        List<Department> departements = departmentDao.findAll();
        return departmentMapper.toDtoList(departements);
    }

    /**
     * Récupère un département par son identifiant
     * @param id identifiant du département
     * @return le DTO du département si trouvé, null sinon
     */
    public DepartmentDto getDepartmentById(Long id) {
        Department departement = departmentDao.findById(id);
        return departmentMapper.toDto(departement);
    }

    /**
     * Récupère un département par son code
     * @param code code du département
     * @return le DTO du département si trouvé, null sinon
     */
    public DepartmentDto getDepartmentByCode(String code) {
        Department departement = departmentDao.findByCode(code);
        return departmentMapper.toDto(departement);
    }

    /**
     * Crée un nouveau département
     * @param departementDto le DTO du département à créer
     * @return le DTO du département créé
     */
    @Transactional
    public DepartmentDto createDepartment(DepartmentDto departementDto) {
        Department departement = departmentMapper.toEntity(departementDto);
        Department departementSauve = departmentDao.save(departement);
        return departmentMapper.toDto(departementSauve);
    }

    /**
     * Met à jour un département existant
     * @param id identifiant du département à modifier
     * @param departmentDto nouvelle représentation du département
     * @return le DTO du département modifié, ou null si non trouvé
     */
    @Transactional
    public DepartmentDto updateDepartment(Long id, DepartmentDto departmentDto) {
        Department departmentExistant = departmentDao.findById(id);
        if (departmentExistant == null) {
            return null;
        }

        departmentMapper.updateEntityFromDto(departmentDto, departmentExistant);
        Department departmentMisAJour = departmentDao.save(departmentExistant);
        return departmentMapper.toDto(departmentMisAJour);
    }

    /**
     * Supprime un département par son identifiant
     * @param id identifiant du département à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    @Transactional
    public boolean deleteDepartmentById(Long id) {
        if (departmentDao.existsById(id)) {
            departmentDao.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Vérifie si un département existe par son identifiant
     * @param id identifiant du département
     * @return true si le département existe, false sinon
     */
    public boolean departmentExistsById(Long id) {
        return departmentDao.existsById(id);
    }

    /**
     * Vérifie si un département existe par son code
     * @param code code du département
     * @return true si le département existe, false sinon
     */
    public boolean departmentExistsByCode(String code) {
        return departmentDao.existsByCode(code);
    }
}