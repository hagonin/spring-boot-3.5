package fr.digi.d202508.springdemo;

import fr.digi.d202508.springdemo.daos.DepartmentDao;
import fr.digi.d202508.springdemo.entities.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Application Spring Boot qui met à jour les noms des départements 
 * en appelant l'API externe geo.api.gouv.fr sans démarrer Tomcat
 */
@SpringBootApplication(exclude = WebMvcAutoConfiguration.class)
@Profile("updater")
public class DepartmentUpdaterApplication implements CommandLineRunner {

    @Autowired
    private DepartmentDao departmentDao;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DepartmentUpdaterApplication.class);
        app.setWebApplicationType(org.springframework.boot.WebApplicationType.NONE);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Démarrage de la mise à jour des départements ===");
        
        try {
            // Appel de l'API externe
            String apiUrl = "https://geo.api.gouv.fr/departements";
            System.out.println("Appel de l'API : " + apiUrl);
            
            String response = restTemplate.getForObject(apiUrl, String.class);
            
            if (response != null) {
                JsonNode departmentsJson = objectMapper.readTree(response);
                System.out.println("Récupération de " + departmentsJson.size() + " départements depuis l'API");
                
                // Récupération des départements en base
                List<Department> departmentsInDb = departmentDao.findAll();
                System.out.println("Départements en base : " + departmentsInDb.size());
                
                int updatedCount = 0;
                
                // Mise à jour des noms des départements
                for (JsonNode deptJson : departmentsJson) {
                    String code = deptJson.get("code").asText();
                    String nom = deptJson.get("nom").asText();
                    
                    // Recherche du département en base par code
                    Department departmentInDb = departmentDao.findByCode(code);
                    
                    if (departmentInDb != null) {
                        // Mise à jour seulement si le nom est différent ou vide
                        if (departmentInDb.getName() == null || 
                            departmentInDb.getName().trim().isEmpty() || 
                            !departmentInDb.getName().equals(nom)) {
                            
                            String oldName = departmentInDb.getName();
                            departmentInDb.setName(nom);
                            departmentDao.save(departmentInDb);
                            updatedCount++;
                            
                            System.out.println("Mis à jour : " + code + " - '" + oldName + "' -> '" + nom + "'");
                        }
                    } else {
                        System.out.println("Département non trouvé en base : " + code + " - " + nom);
                    }
                }
                
                System.out.println("=== Mise à jour terminée ===");
                System.out.println("Départements mis à jour : " + updatedCount);
                
            } else {
                System.err.println("Erreur : Réponse vide de l'API");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour des départements : " + e.getMessage());
            e.printStackTrace();
        }
    }
}