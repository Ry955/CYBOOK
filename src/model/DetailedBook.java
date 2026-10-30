package model; // Déclaration du package nommé 'model'.

/**
 * Représente un livre détaillé avec des informations supplémentaires telles que le titre, l'auteur,
 * la date de publication, l'édition, la collection et la disponibilité.
 */
public class DetailedBook extends Book { // La classe DetailedBook hérite de la classe Book.

    private String title; // Titre du livre
    private String author; // Auteur du livre
    private String publicationDate; // Date de publication du livre
    private String edition; // Édition du livre
    private String collection; // Collection à laquelle appartient le livre
    private String availability; // Statut de disponibilité du livre

    /**
     * Constructeur pour initialiser un livre détaillé avec ses attributs.
     *
     * @param isbn            L'ISBN du livre.
     * @param title           Le titre du livre.
     * @param author          L'auteur du livre.
     * @param publicationDate La date de publication du livre.
     * @param edition         L'édition du livre.
     * @param collection      La collection à laquelle le livre appartient.
     * @param stock           Le stock du livre.
     * @param availability    Le statut de disponibilité du livre.
     */
    public DetailedBook(String isbn, String title, String author, String publicationDate, String edition, String collection, int stock, String availability) {
        super(isbn, stock); // Appel au constructeur de la classe mère (Book) avec l'ISBN et le stock.
        this.title = title; // Initialise le titre du livre.
        this.author = author; // Initialise l'auteur du livre.
        this.publicationDate = publicationDate; // Initialise la date de publication du livre.
        this.edition = edition; // Initialise l'édition du livre.
        this.collection = collection; // Initialise la collection du livre.
        this.availability = availability; // Initialise la disponibilité du livre.
    }

    /**
     * Obtient la collection à laquelle appartient le livre.
     *
     * @return La collection du livre.
     */
    public String getCollection() {
        return collection; // Retourne la collection du livre.
    }

    /**
     * Définit la collection à laquelle le livre appartient.
     *
     * @param collection La nouvelle collection du livre.
     */
    public void setCollection(String collection) {
        this.collection = collection; // Met à jour la collection du livre.
    }

    /**
     * Obtient le statut de disponibilité du livre.
     *
     * @return Le statut de disponibilité du livre.
     */
    public String getAvailability() {
        return availability; // Retourne la disponibilité du livre.
    }

    /**
     * Définit le statut de disponibilité du livre.
     *
     * @param availability Le nouveau statut de disponibilité du livre.
     */
    public void setAvailability(String availability) {
        this.availability = availability; // Met à jour la disponibilité du livre.
    }

    /**
     * Obtient l'édition du livre.
     *
     * @return L'édition du livre.
     */
    public String getEdition() {
        return edition; // Retourne l'édition du livre.
    }

    /**
     * Définit l'édition du livre.
     *
     * @param edition La nouvelle édition du livre.
     */
    public void setEdition(String edition) {
        this.edition = edition; // Met à jour l'édition du livre.
    }

    /**
     * Obtient le titre du livre.
     *
     * @return Le titre du livre.
     */
    public String getTitle() {
        return title; // Retourne le titre du livre.
    }

    /**
     * Définit le titre du livre.
     *
     * @param title Le nouveau titre du livre.
     */
    public void setTitle(String title) {
        this.title = title; // Met à jour le titre du livre.
    }

    /**
     * Obtient l'auteur du livre.
     *
     * @return L'auteur du livre.
     */
    public String getAuthor() {
        return author; // Retourne l'auteur du livre.
    }

    /**
     * Définit l'auteur du livre.
     *
     * @param author Le nouvel auteur du livre.
     */
    public void setAuthor(String author) {
        this.author = author; // Met à jour l'auteur du livre.
    }

    /**
     * Obtient la date de publication du livre.
     *
     * @return La date de publication du livre.
     */
    public String getPublicationDate() {
        return publicationDate; // Retourne la date de publication du livre.
    }

    /**
     * Définit la date de publication du livre.
     *
     * @param publicationDate La nouvelle date de publication du livre.
     */
    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate; // Met à jour la date de publication du livre.
    }
}
