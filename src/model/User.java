package model;

/**
 * Représente un utilisateur dans le système de bibliothèque, contenant des informations telles que l'ID,
 * le nom, le prénom, l'adresse email, l'adresse postale et le numéro de téléphone.
 */
public class User {
    private int id; // Identifiant unique de l'utilisateur
    private String lastName; // Nom de famille de l'utilisateur
    private String firstName; // Prénom de l'utilisateur
    private String email; // Adresse email de l'utilisateur
    private String address; // Adresse postale de l'utilisateur
    private String phone; // Numéro de téléphone de l'utilisateur

    /**
     * Constructeur de l'objet User avec les détails spécifiés.
     *
     * @param id        Identifiant unique de l'utilisateur.
     * @param lastName  Nom de famille de l'utilisateur.
     * @param firstName Prénom de l'utilisateur.
     * @param email     Adresse email de l'utilisateur.
     * @param address   Adresse postale de l'utilisateur.
     * @param phone     Numéro de téléphone de l'utilisateur.
     */
    public User(int id, String lastName, String firstName, String email, String address, String phone) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }

    /**
     * Retourne l'adresse de l'utilisateur.
     *
     * @return L'adresse de l'utilisateur.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Définit l'adresse de l'utilisateur.
     *
     * @param address L'adresse à définir pour l'utilisateur.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Retourne le numéro de téléphone de l'utilisateur.
     *
     * @return Le numéro de téléphone de l'utilisateur.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Définit le numéro de téléphone de l'utilisateur.
     *
     * @param phone Le numéro de téléphone à définir pour l'utilisateur.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Retourne l'identifiant unique de l'utilisateur.
     *
     * @return L'identifiant unique de l'utilisateur.
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de l'utilisateur.
     *
     * @param id L'identifiant unique à définir pour l'utilisateur.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retourne le nom de famille de l'utilisateur.
     *
     * @return Le nom de famille de l'utilisateur.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Définit le nom de famille de l'utilisateur.
     *
     * @param lastName Le nom de famille à définir pour l'utilisateur.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Retourne le prénom de l'utilisateur.
     *
     * @return Le prénom de l'utilisateur.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Définit le prénom de l'utilisateur.
     *
     * @param firstName Le prénom à définir pour l'utilisateur.
     */
    public void setFirstName(String firstName) {
    this.firstName = firstName;
    }

    /**
     * Retourne l'adresse email de l'utilisateur.
     *
     * @return L'adresse email de l'utilisateur.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Définit l'adresse email de l'utilisateur.
     *
     * @param email L'adresse email à définir pour l'utilisateur.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retourne une représentation en chaîne de caractères de l'utilisateur,
     * incluant le prénom, le nom et l'ID.
     *
     * @return Une chaîne représentant l'utilisateur.
     */
    @Override
    public String toString() {
        return firstName + " " + lastName + " [ID: " + id + "]";
    }
}
