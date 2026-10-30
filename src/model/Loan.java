package model; // Déclaration du package nommé 'model'.

import java.util.Date; // Importe la classe Date de la bibliothèque Java pour manipuler les dates.

/**
 * Représente un prêt dans le système de bibliothèque, contenant des informations sur l'utilisateur, le livre, 
 * la date de prêt, la date de retour prévue et le statut de retour.
 */
public class Loan {
    private int id; // Identifiant unique du prêt
    private User user; // Utilisateur qui a emprunté le livre
    private Book book; // Livre qui a été emprunté
    private Date loanDate; // Date à laquelle le livre a été emprunté
    private Date dueDate; // Date de retour prévue
    private int returned; // Statut de retour : 0 pour non retourné, 1 pour retourné

    /**
     * Construit un objet Loan avec les détails spécifiés.
     *
     * @param id       Identifiant unique pour le prêt.
     * @param user     Utilisateur qui a emprunté le livre.
     * @param book     Livre qui a été emprunté.
     * @param loanDate Date à laquelle le livre a été emprunté.
     * @param dueDate  Date de retour prévue.
     * @param returned Statut de retour : 0 pour non retourné, 1 pour retourné.
     */
    public Loan(int id, User user, Book book, Date loanDate, Date dueDate, int returned) {
        this.id = id; // Initialise l'identifiant unique du prêt.
        this.user = user; // Initialise l'utilisateur qui a emprunté le livre.
        this.book = book; // Initialise le livre emprunté.
        this.loanDate = loanDate; // Initialise la date de prêt du livre.
        this.dueDate = dueDate; // Initialise la date de retour prévue.
        this.returned = returned; // Initialise le statut de retour du prêt.
    }

    /**
     * Obtient l'identifiant unique du prêt.
     *
     * @return L'identifiant unique du prêt.
     */
    public int getId() {
        return id; // Retourne l'identifiant unique du prêt.
    }

    /**
     * Définit l'identifiant unique du prêt.
     *
     * @param id L'identifiant unique du prêt.
     */
    public void setId(int id) {
        this.id = id; // Met à jour l'identifiant unique du prêt.
    }

    /**
     * Obtient l'utilisateur qui a emprunté le livre.
     *
     * @return L'utilisateur qui a emprunté le livre.
     */
    public User getUser() {
        return user; // Retourne l'utilisateur qui a emprunté le livre.
    }

    /**
     * Définit l'utilisateur qui a emprunté le livre.
     *
     * @param user L'utilisateur qui a emprunté le livre.
     */
    public void setUser(User user) {
        this.user = user; // Met à jour l'utilisateur qui a emprunté le livre.
    }

    /**
     * Obtient le livre qui a été emprunté.
     *
     * @return Le livre qui a été emprunté.
     */
    public Book getBook() {
        return book; // Retourne le livre emprunté.
    }

    /**
     * Définit le livre qui a été emprunté.
     *
     * @param book Le livre qui a été emprunté.
     */
    public void setBook(Book book) {
        this.book = book; // Met à jour le livre emprunté.
    }

    /**
     * Obtient la date à laquelle le livre a été emprunté.
     *
     * @return La date à laquelle le livre a été emprunté.
     */
    public Date getLoanDate() {
        return loanDate; // Retourne la date de prêt du livre.
    }

    /**
     * Définit la date à laquelle le livre a été emprunté.
     *
     * @param loanDate La date à laquelle le livre a été emprunté.
     */
    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate; // Met à jour la date de prêt du livre.
    }

    /**
     * Obtient la date de retour prévue.
     *
     * @return La date de retour prévue.
     */
    public Date getDueDate() {
        return dueDate; // Retourne la date de retour prévue du livre.
    }

    /**
     * Définit la date de retour prévue.
     *
     * @param dueDate La date de retour prévue.
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate; // Met à jour la date de retour prévue.
    }

    /**
     * Obtient le statut de retour du livre.
     *
     * @return Le statut de retour : 0 pour non retourné, 1 pour retourné.
     */
    public int getReturned() {
        return returned; // Retourne le statut de retour du prêt.
    }

    /**
     * Définit le statut de retour du livre.
     *
     * @param returned Le statut de retour : 0 pour non retourné, 1 pour retourné.
     */
    public void setReturned(int returned) {
        this.returned = returned; // Met à jour le statut de retour.
    }
}
