package fr.digi.d202508.springdemo.services;

import org.springframework.stereotype.Service;

/**
 * Service applicatif fournissant un message de salutation.
 */
@Service
public class HelloService {
    /**
     * Retourne un message de salutation générique.
     * @return le message de salutation
     */
    public String salutations() {
        return "Je suis la classe de service et je vous dis Bonjour";
    }
}
