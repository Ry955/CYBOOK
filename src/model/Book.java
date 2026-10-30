package model; // Déclaration du package nommé 'model'.

import api.BNFApiClient; // Importe la classe BNFApiClient située dans le package 'api'.

/**
 * Représente un livre avec son ISBN, son stock, et le nombre de prêts.
 */
public class Book {
    private BNFApiClient apiClient = new BNFApiClient(); // Crée une instance de BNFApiClient pour l'utilisation d'une API.
    protected String isbn; // Variable protégée stockant l'ISBN du livre.
    protected int stock; // Variable protégée représentant le nombre d'exemplaires du livre en stock.
    protected int loansCount; // Variable protégée indiquant le nombre de fois que le livre a été prêté.

    /**
     * Constructeur pour initialiser un livre avec son ISBN et son stock.
     *
     * @param isbn  L'ISBN du livre.
     * @param stock Le stock du livre.
     */
    public Book(String isbn, int stock) {
        this.isbn = isbn; // Initialise l'ISBN du livre avec la valeur passée en argument.
        this.stock = stock; // Initialise le stock du livre avec la valeur passée en argument.
    }

    /**
     * Obtient l'ISBN du livre.
     *
     * @return L'ISBN du livre.
     */
    public String getIsbn() {
        return isbn; // Retourne l'ISBN du livre.
    }

    /**
     * Définit l'ISBN du livre.
     *
     * @param isbn Le nouvel ISBN du livre.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn; // Met à jour l'ISBN du livre avec la nouvelle valeur fournie.
    }

    /**
     * Obtient le titre du livre en le récupérant via l'API BNF.
     *
     * @return Le titre du livre.
     */
    public String getTitle() {
        return apiClient.fetchTitle(this.isbn); // Retourne le titre du livre en utilisant l'ISBN pour faire une requête à l'API.
    }

    /**
     * Obtient l'auteur du livre en le récupérant via l'API BNF.
     *
     * @return L'auteur du livre.
     */
    public String getAuthor() {
        return apiClient.fetchAuthor(this.isbn); // Retourne l'auteur du livre en utilisant l'ISBN pour faire une requête à l'API.
    }

    /**
     * Définit l'auteur du livre. Cette méthode définit en fait l'ISBN, ce qui semble être une erreur.
     *
     * @param author L'auteur du livre.
     */
    public void setAuthor(String author) {
        this.isbn = author; // Incorrectement mis à jour de l'ISBN avec la valeur destinée à l'auteur.
    }

    /**
     * Obtient le stock du livre.
     *
     * @return Le stock du livre.
     */
    public int getStock() {
        return stock; // Retourne le nombre d'exemplaires du livre disponibles en stock.
    }

    /**
     * Définit le stock du livre.
     *
     * @param stock Le nouveau stock du livre.
     */
    public void setStock(int stock) {
        this.stock = stock; // Met à jour le stock du livre avec la nouvelle valeur fournie.
    }

    /**
     * Obtient le nombre de prêts du livre.
     *
     * @return Le nombre de fois que le livre a été prêté.
     */
    public int getLoansCount() {
        return loansCount; // Retourne le nombre de fois que le livre a été prêté.
    }

    /**
     * Définit le nombre de prêts du livre.
     *
     * @param loansCount Le nouveau nombre de prêts du livre.
     */
    public void setLoansCount(int loansCount) {
        this.loansCount = loansCount; // Met à jour le nombre de prêts du livre avec la nouvelle valeur fournie.
    }

    /**
     * Retourne la représentation en chaîne de caractères du livre.
     *
     * @return L'ISBN du livre.
     */
    @Override
    public String toString() {
        return isbn; // Retourne l'ISBN du livre sous forme de chaîne de caractères.
    }
}
