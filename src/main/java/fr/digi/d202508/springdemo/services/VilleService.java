package fr.digi.d202508.springdemo.services;

import fr.digi.d202508.springdemo.daos.VilleDao;
import fr.digi.d202508.springdemo.entities.Ville;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service gérant la logique métier des opérations sur les villes.
 */
@Service
@Transactional(readOnly = true)
public class VilleService {

    @Autowired
    private VilleDao villeDao;

    /**
     * Extrait et retourne la liste de toutes les villes.
     * @return la liste des villes
     */
    public List<Ville> getAllCities() {
        return villeDao.findAll();
    }

    /**
     * Récupère une ville par son identifiant.
     * @param idVille identifiant de la ville
     * @return la ville si trouvée, null sinon
     */
    public Ville getCityById(int idVille) {
        return villeDao.findById((long) idVille);
    }

    /**
     * Récupère une ville par son nom.
     * @param nom nom de la ville
     * @return la ville si trouvée, null sinon
     */
    public Ville getCityByName(String nom) {
        return villeDao.findByName(nom);
    }

    /**
     * Insère une nouvelle ville et retourne la liste mise à jour.
     * @param ville la ville à insérer
     * @return la liste des villes après ajout
     */
    @Transactional
    public List<Ville> createCity(Ville ville) {
        villeDao.save(ville);
        return villeDao.findAll();
    }

    /**
     * Modifie une ville par son identifiant et retourne la liste mise à jour.
     * @param idVille identifiant de la ville à modifier
     * @param villeModifiee nouvelle représentation de la ville
     * @return la liste des villes après modification
     */
    @Transactional
    public List<Ville> updateCity(int idVille, Ville villeModifiee) {
        if (villeDao.existsById((long) idVille)) {
            villeModifiee.setId((long) idVille);
            villeDao.save(villeModifiee);
        }
        return villeDao.findAll();
    }

    /**
     * Supprime une ville par son identifiant et retourne la liste mise à jour.
     * @param idVille identifiant de la ville à supprimer
     * @return la liste des villes après suppression
     */
    @Transactional
    public List<Ville> deleteCity(int idVille) {
        if (villeDao.existsById((long) idVille)) {
            villeDao.deleteById((long) idVille);
        }
        return villeDao.findAll();
    }

    /**
     * Récupère les N villes les plus peuplées d'un département.
     * @param departementCode code du département
     * @param n nombre de villes à récupérer
     * @return la liste des villes triées par population décroissante
     */
    public List<Ville> getTopCitiesByDepartment(String departementCode, int n) {
        return villeDao.findTopCitiesByDepartementCode(departementCode, n);
    }

    /**
     * Récupère les villes d'un département dans une tranche de population.
     * @param departementCode code du département
     * @param minPopulation population minimale
     * @param maxPopulation population maximale
     * @return la liste des villes dans la tranche de population
     */
    public List<Ville> getCitiesByDepartmentAndPopulationRange(String departementCode, int minPopulation, int maxPopulation) {
        return villeDao.findCitiesByDepartementCodeAndPopulationRange(departementCode, minPopulation, maxPopulation);
    }
}