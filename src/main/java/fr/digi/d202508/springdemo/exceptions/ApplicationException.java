package fr.digi.d202508.springdemo.exceptions;

/**
 * Exception applicative contrôlée pour signaler des erreurs métier ou de validation
 * qui doivent être gérées explicitement par la couche web
 */
public class ApplicationException extends Exception {

    /**
     * Crée une nouvelle ApplicationException avec un message
     * @param message description de l'erreur
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Crée une nouvelle ApplicationException avec un message et une cause
     * @param message description de l'erreur
     * @param cause cause initiale
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}

