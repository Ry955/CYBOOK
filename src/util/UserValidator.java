package util;

/**
 * Classe utilitaire pour la validation d'informations utilisateur.
 * Elle fournit des méthodes pour valider des noms, des adresses email, des adresses postales, et des numéros de téléphone.
 */
public class UserValidator {
    
    /**
     * Constructeur par défaut.
     * Il est vide car il n'y a pas de variables d'instance à initialiser spécifiquement pour cette classe.
     */
    public UserValidator() {
        // Le constructeur par défaut est principalement utilisé pour créer des instances de UserValidator si nécessaire.
    }

    /**
     * Valide un nom d'utilisateur (prénom ou nom de famille).
     *
     * @param name Le nom à valider.
     * @return Vrai si le nom est valide, faux sinon.
     */
    public static boolean isValidName(String name) {
        // Utilisation d'une expression régulière qui accepte les lettres (y compris les lettres accentuées), les tirets, les apostrophes et les espaces.
        String regex = "^[\\p{L} '-]+$";
        return name.matches(regex);
    }

    /**
     * Valide une adresse email.
     *
     * @param email L'adresse email à valider.
     * @return Vrai si l'email est valide, faux sinon.
     */
    public static boolean isValidEmail(String email) {
        // Expression régulière pour valider les adresses email en utilisant des caractères standards et permettant des sous-domaines.
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }

    /**
     * Valide une adresse postale.
     *
     * @param address L'adresse à valider.
     * @return Vrai si l'adresse est valide, faux sinon.
     */
    public static boolean isValidAddress(String address) {
        // Expression régulière simple pour la validation des adresses, acceptant les lettres, les chiffres, les points, les virgules, les apostrophes, les tirets et les barres obliques.
        String regex = "^[\\p{L}0-9 .,'/-]+$";
        return address.matches(regex);
    }

    /**
     * Valide un numéro de téléphone dans le format français.
     *
     * @param phone Le numéro de téléphone à valider.
     * @return Vrai si le numéro de téléphone est valide, faux sinon.
     */
    public static boolean isValidPhone(String phone) {
        // Permet les formats avec ou sans espaces, avec ou sans le préfixe international.
        // Le regex vérifie que le numéro commence par +33 suivi de 1-9 et ensuite de huit chiffres,
        // ou commence par un 0 suivi de 1-9 et ensuite de huit chiffres, divisés en blocs de deux.
        String regex = "^(\\+33[1-9]|0[1-9])(\\d{2}){4}$";
        return phone.matches(regex);  // Retourne vrai si le numéro de téléphone correspond au motif.
    }
}
