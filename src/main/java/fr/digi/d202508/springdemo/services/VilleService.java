package fr.digi.d202508.springdemo.services;

public class VilleService {
    private String nom;
    private int nbHabitants;

    public VilleService(String nom, int nbHabitants) {
        this.nom = nom;
        this.nbHabitants = nbHabitants;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
}
