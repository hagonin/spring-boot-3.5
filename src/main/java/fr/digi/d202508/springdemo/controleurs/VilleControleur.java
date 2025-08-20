package fr.digi.d202508.springdemo.controleurs;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/ville")
public class VilleControleur {

    @GetMapping
    public List<String> getVilles() {
        return Arrays.asList(
            "Montpellier",
            "Paris", 
            "Lyon",
            "Marseille",
            "Toulouse",
            "Nantes",
            "Bordeaux",
            "Strasbourg"
        );
    }

}
