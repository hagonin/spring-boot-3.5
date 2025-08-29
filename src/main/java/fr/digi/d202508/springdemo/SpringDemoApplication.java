package fr.digi.d202508.springdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

/**
 * Point d'entrée de l'application Spring Boot
 */
@SpringBootApplication
@Profile("default")
public class SpringDemoApplication {

    /**
     * Démarre l'application Spring Boot.
     * @param args arguments de ligne de commande
     */
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "default");
        SpringApplication.run(SpringDemoApplication.class, args);
    }

}
