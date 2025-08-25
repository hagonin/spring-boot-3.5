package fr.digi.d202508.springdemo.services;

import fr.digi.d202508.springdemo.daos.DepartmentDao;
import fr.digi.d202508.springdemo.dtos.DepartmentDto;
import fr.digi.d202508.springdemo.entities.Department;
import fr.digi.d202508.springdemo.exceptions.ApplicationException;
import fr.digi.d202508.springdemo.mappers.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;


@Service
@Transactional(readOnly = true)
public class DepartmentService {

    @Autowired
    private DepartmentDao departmentDao;
    
    @Autowired
    private DepartmentMapper departmentMapper;
    
    @Autowired
    private MessageSource messageSource;
    
    private static final Pattern DEPARTMENT_CODE_PATTERN = Pattern.compile("^[0-9A-Za-z]{2,3}$");

    /**
     * Récupère tous les départements
     * @return la liste des DTOs des départements
     */
    public List<DepartmentDto> getAllDepartments() {
        List<Department> departments = departmentDao.findAll();
        return departmentMapper.toDtoList(departments);
    }

    /**
     * Récupère un département par son identifiant
     * @param id identifiant du département
     * @return le DTO du département si trouvé
     * @throws ApplicationException si le département n'existe pas ou l'ID est invalide
     */
    public DepartmentDto getDepartmentById(Long id) throws ApplicationException {
        if (id == null || id <= 0) {
            throw new ApplicationException("L'identifiant du département doit être un nombre positif");
        }
        Department department = departmentDao.findById(id);
        if (department == null) {
            throw new ApplicationException(messageSource.getMessage("departement.not.found.id", new Object[]{id}, LocaleContextHolder.getLocale()));
        }
        return departmentMapper.toDto(department);
    }

    /**
     * Récupère un département par son code
     * @param code code du département
     * @return le DTO du département si trouvé
     * @throws ApplicationException si le département n'existe pas ou le code est invalide
     */
    public DepartmentDto getDepartmentByCode(String code) throws ApplicationException {
        if (code == null || code.trim().isEmpty()) {
            throw new ApplicationException(messageSource.getMessage("departement.code.notblank", null, LocaleContextHolder.getLocale()));
        }
        if (!DEPARTMENT_CODE_PATTERN.matcher(code.trim()).matches()) {
            throw new ApplicationException(messageSource.getMessage("departement.code.invalid.format", null, LocaleContextHolder.getLocale()));
        }
        Department department = departmentDao.findByCode(code.trim());
        if (department == null) {
            throw new ApplicationException(messageSource.getMessage("departement.not.found.code", new Object[]{code}, LocaleContextHolder.getLocale()));
        }
        return departmentMapper.toDto(department);
    }

    /**
     * Crée un nouveau département
     * @param departmentDto le DTO du département à créer
     * @return le DTO du département créé
     * @throws ApplicationException si les données sont invalides
     */
    @Transactional
    public DepartmentDto createDepartment(DepartmentDto departmentDto) throws ApplicationException {
        validateDepartmentData(departmentDto);
        
        // Vérifier que le code n'existe pas déjà
        if (departmentDao.existsByCode(departmentDto.getCode().trim())) {
            throw new ApplicationException(messageSource.getMessage("departement.code.duplicate", new Object[]{departmentDto.getCode()}, LocaleContextHolder.getLocale()));
        }
        
        Department department = departmentMapper.toEntity(departmentDto);
        Department departmentSaved = departmentDao.save(department);
        return departmentMapper.toDto(departmentSaved);
    }

    /**
     * Met à jour un département existant
     * @param id identifiant du département à modifier
     * @param departmentDto nouvelle représentation du département
     * @return le DTO du département modifié
     * @throws ApplicationException si les données sont invalides ou le département n'existe pas
     */
    @Transactional
    public DepartmentDto updateDepartment(Long id, DepartmentDto departmentDto) throws ApplicationException {
        if (id == null || id <= 0) {
            throw new ApplicationException("L'identifiant du département doit être un nombre positif");
        }
        
        validateDepartmentData(departmentDto);
        
        Department departmentExisted = departmentDao.findById(id);
        if (departmentExisted == null) {
            throw new ApplicationException(messageSource.getMessage("departement.not.found.id", new Object[]{id}, LocaleContextHolder.getLocale()));
        }
        
        // Vérifier qu'aucun autre département n'a le même code
        Department departmentWithSameCode = departmentDao.findByCode(departmentDto.getCode().trim());
        if (departmentWithSameCode != null && !departmentWithSameCode.getId().equals(id)) {
            throw new ApplicationException(messageSource.getMessage("departement.code.duplicate", new Object[]{departmentDto.getCode()}, LocaleContextHolder.getLocale()));
        }

        departmentMapper.updateEntityFromDto(departmentDto, departmentExisted);
        Department departmentUpdated = departmentDao.save(departmentExisted);
        return departmentMapper.toDto(departmentUpdated);
    }

    /**
     * Supprime un département par son identifiant
     * @param id identifiant du département à supprimer
     * @return true si la suppression a réussi
     * @throws ApplicationException si l'ID est invalide ou le département n'existe pas
     */
    @Transactional
    public boolean deleteDepartmentById(Long id) throws ApplicationException {
        if (id == null || id <= 0) {
            throw new ApplicationException("L'identifiant du département doit être un nombre positif");
        }
        if (!departmentDao.existsById(id)) {
            throw new ApplicationException(messageSource.getMessage("departement.not.found.id", new Object[]{id}, LocaleContextHolder.getLocale()));
        }
        departmentDao.deleteById(id);
        return true;
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
    
    /**
     * Valide les données d'un département
     * @param departmentDto les données du département à valider
     * @throws ApplicationException si les données sont invalides
     */
    private void validateDepartmentData(DepartmentDto departmentDto) throws ApplicationException {
        if (departmentDto == null) {
            throw new ApplicationException("Les données du département ne peuvent pas être nulles");
        }
        
        if (departmentDto.getCode() == null || departmentDto.getCode().trim().isEmpty()) {
            throw new ApplicationException(messageSource.getMessage("departement.code.notblank", null, LocaleContextHolder.getLocale()));
        }
        
        if (!DEPARTMENT_CODE_PATTERN.matcher(departmentDto.getCode().trim()).matches()) {
            throw new ApplicationException(messageSource.getMessage("departement.code.invalid.format", null, LocaleContextHolder.getLocale()));
        }
        
        if (departmentDto.getName() == null || departmentDto.getName().trim().isEmpty()) {
            throw new ApplicationException(messageSource.getMessage("departement.nom.notblank", null, LocaleContextHolder.getLocale()));
        }
        
        if (departmentDto.getPopulation() != null && departmentDto.getPopulation() < 0) {
            throw new ApplicationException(messageSource.getMessage("departement.population.negative", null, LocaleContextHolder.getLocale()));
        }
    }
}