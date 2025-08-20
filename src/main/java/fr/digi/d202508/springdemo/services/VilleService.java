package fr.digi.d202508.springdemo.services;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Représentation d'une ville avec validation des champs pour les opérations REST.
 */
public class VilleService {
    @Positive(message = "L'id doit être strictement positif")
    private int id;
    
    @NotBlank(message = "Le nom de la ville ne peut pas être nul")
    @Size(min = 2, message = "Le nom de la ville doit avoir au moins 2 caractères")
    private String nom;
    
    @Min(value = 1, message = "Le nombre d'habitants doit être supérieur ou égal à 1")
    private int nbHabitants;

    /**
     * Construit une ville.
     *
     * @param id identifiant strictement positif
     * @param nom nom de la ville
     * @param nbHabitants nombre d'habitants (>= 1)
     */
    public VilleService(int id, String nom, int nbHabitants) {
        this.id = id;
        this.nom = nom;
        this.nbHabitants = nbHabitants;
    }
    /**
     * Nom de la ville.
     *
     * @return le nom
     */
    public String getNom() {
        return nom;
    }
    /**
     * Modifie le nom de la ville.
     *
     * @param nom nouveau nom
     */
    public void setNom(String nom) {
        this.nom = nom;
    }
    /**
     * Nombre d'habitants.
     *
     * @return le nombre d'habitants
     */
    public int getNbHabitants() {
        return nbHabitants;
    }
    /**
     * Modifie le nombre d'habitants.
     *
     * @param nbHabitants nouveau nombre d'habitants
     */
    public void setNbHabitants(int nbHabitants) {
        this.nbHabitants = nbHabitants;
    }
    /**
     * Identifiant de la ville.
     *
     * @return l'identifiant
     */
    public int getId() {
        return id;
    }
    /**
     * Modifie l'identifiant de la ville.
     *
     * @param id nouvel identifiant
     */
    public void setId(int id) {
        this.id = id;
    }
}
