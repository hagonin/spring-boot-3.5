package fr.digi.d202508.springdemo.services;

public class VilleService {
    private int id;
    private String nom;
    private int nbHabitants;

    public VilleService(int id, String nom, int nbHabitants) {
        this.id = id;
        this.nom = nom;
        this.nbHabitants = nbHabitants;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public int getNbHabitants() {
        return nbHabitants;
    }
    public void setNbHabitants(int nbHabitants) {
        this.nbHabitants = nbHabitants;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
