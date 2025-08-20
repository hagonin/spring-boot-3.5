package fr.digi.d202508.springdemo.controleurs;

import fr.digi.d202508.springdemo.services.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
/**
 * Contrôleur REST exposant un endpoint simple pour saluer l'utilisateur.
 */
public class HelloControleur {
    
    private final HelloService helloService;
    
    /**
     * Construit le contrôleur avec son service associé.
     *
     * @param helloService service fournissant le message de salutation
     */
    public HelloControleur(HelloService helloService) {
        this.helloService = helloService;
    }
    
    @GetMapping
    /**
     * Retourne un message de salutation.
     *
     * @return le message de salutation
     */
    public String direHello(){
        return helloService.salutations();
    }
}