package fr.digi.d202508.springdemo.controllers;

import fr.digi.d202508.springdemo.dtos.DepartmentDto;
import fr.digi.d202508.springdemo.exceptions.ApplicationException;
import fr.digi.d202508.springdemo.services.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private MessageSource messageSource;

    /**
     * Valide la requête et retourne une réponse d'erreur le cas échéant
     * @param result résultat de la validation
     * @return une réponse 400 si erreurs, sinon null
     */
    private ResponseEntity<String> validateDepartment(BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            result.getFieldErrors().forEach(error ->
                errors.append(error.getDefaultMessage()).append("; ")
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }
        return null;
    }

    /**
     * Récupère la liste complète des départements
     * @return la liste des départements
     */
    @GetMapping
    public List<DepartmentDto> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    /**
     * Récupère un département par son identifiant
     * @param id identifiant du département
     * @return 200 avec le département si trouvé, 404 sinon
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable Long id) throws ApplicationException {
        DepartmentDto department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    /**
     * Récupère un département par son code
     * @param code code du département
     * @return 200 avec le département si trouvé, 404 sinon
     */
    @GetMapping("/code-department/{code}")
    public ResponseEntity<DepartmentDto> getDepartmentByCode(@PathVariable String code) throws ApplicationException {
        DepartmentDto department = departmentService.getDepartmentByCode(code);
        return ResponseEntity.ok(department);
    }

    /**
     * Ajoute un nouveau département après validation
     * @param newDepartment le département à ajouter
     * @param result résultat de la validation de la requête
     * @return 201 si ajout réussi, 400 si erreurs de validation ou doublons
     */
    @PostMapping
    public ResponseEntity<?> createDepartment(@Valid @RequestBody DepartmentDto newDepartment, BindingResult result) throws ApplicationException {
        ResponseEntity<String> validationError = validateDepartment(result);
        if (validationError != null) {
            return validationError;
        }
        
        DepartmentDto departmentCreated = departmentService.createDepartment(newDepartment);
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentCreated);
    }

    /**
     * Met à jour un département existant
     * @param id identifiant du département à modifier
     * @param departmentModified nouvelle représentation du département
     * @param result résultat de la validation
     * @return 200 si succès, 400 si erreurs de validation, 404 si le département n'existe pas
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentDto departmentModified, BindingResult result) throws ApplicationException {
        ResponseEntity<String> validationError = validateDepartment(result);
        if (validationError != null) {
            return validationError;
        }
        
        DepartmentDto departmentUpdated = departmentService.updateDepartment(id, departmentModified);
        return ResponseEntity.ok(departmentUpdated);
    }

    /**
     * Supprime un département par son identifiant
     * @param id identifiant du département à supprimer
     * @return 200 si supprimé, 404 si introuvable
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) throws ApplicationException {
        departmentService.deleteDepartmentById(id);
        return ResponseEntity.ok("Département supprimé avec succès");
    }
}