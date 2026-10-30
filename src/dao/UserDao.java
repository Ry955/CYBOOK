package dao;

import java.util.List;
import java.util.ArrayList;
import model.User;
import util.databaseManager;
import util.UserValidator;
import java.sql.*;

public class UserDao {
    private Connection conn; // Connection à la base de données.

    /**
     * Constructeur de UserDao.
     * Initialise la connexion à la base de données via databaseManager.
     */
    public UserDao() {
        this.conn = databaseManager.getConnection(); // Établissement de la connexion à la base de données.
    }

    /**
     * Ajoute un utilisateur à la base de données après validation des données.
     * Les validations incluent le nom, le prénom et l'adresse email.
     *
     * @param user L'utilisateur à ajouter.
     * @return true si l'utilisateur est ajouté avec succès, false en cas d'erreur.
     */
    public boolean addUser(User user) {
        // Validation des données de l'utilisateur avant insertion.
        if (!UserValidator.isValidName(user.getFirstName()) || !UserValidator.isValidName(user.getLastName()) || !UserValidator.isValidEmail(user.getEmail())) {
            System.out.println("Erreur: Données utilisateur non valides.");
            return false;
        }

        String sql = "INSERT INTO users (lastname, firstname, email, address, phone) VALUES (?, ?, ?, ?, ?)"; // SQL pour insérer un utilisateur.
        try (PreparedStatement stmt = conn.prepareStatement(sql)) { // Préparation de la requête SQL.
            stmt.setString(1, user.getLastName()); // Configuration du nom de famille.
            stmt.setString(2, user.getFirstName()); // Configuration du prénom.
            stmt.setString(3, user.getEmail()); // Configuration de l'email.
            stmt.setString(4, user.getAddress()); // Configuration de l'adresse.
            stmt.setString(5, user.getPhone()); // Configuration du téléphone.
            stmt.executeUpdate(); // Exécution de la requête.
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Met à jour les informations d'un utilisateur existant dans la base de données.
     * Les données sont également validées avant la mise à jour.
     *
     * @param user L'utilisateur dont les informations doivent être mises à jour.
     * @return true si la mise à jour réussit, false en cas d'échec.
     */
    public boolean updateUser(User user) {
        // Validation des données de l'utilisateur avant mise à jour.
        if (!UserValidator.isValidName(user.getFirstName()) || !UserValidator.isValidName(user.getLastName()) || !UserValidator.isValidEmail(user.getEmail())) {
            System.out.println("Erreur: Données utilisateur non valides.");
            return false;
        }

        String sql = "UPDATE users SET lastname = ?, firstname = ?, email = ?, address = ?, phone = ? WHERE id = ?"; // SQL pour mettre à jour un utilisateur.
        try (PreparedStatement stmt = conn.prepareStatement(sql)) { // Préparation de la requête SQL.
            stmt.setString(1, user.getLastName()); // Configuration du nom de famille.
            stmt.setString(2, user.getFirstName()); // Configuration du prénom.
            stmt.setString(3, user.getEmail()); // Configuration de l'email.
            stmt.setString(4, user.getAddress()); // Configuration de l'adresse.
            stmt.setString(5, user.getPhone()); // Configuration du téléphone.
            stmt.setInt(6, user.getId()); // Configuration de l'identifiant.
            stmt.executeUpdate(); // Exécution de la requête.
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime un utilisateur de la base de données en utilisant son identifiant.
     *
     * @param userId L'identifiant de l'utilisateur à supprimer.
     * @return true si l'utilisateur est supprimé avec succès, false sinon.
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?"; // SQL pour supprimer un utilisateur.
        try (PreparedStatement stmt = conn.prepareStatement(sql)) { // Préparation de la requête SQL.
            stmt.setInt(1, userId); // Configuration de l'identifiant.
            stmt.executeUpdate(); // Exécution de la requête.
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Récupère tous les utilisateurs de la base de données et les retourne sous forme de liste.
     *
     * @return Une liste de tous les utilisateurs récupérés.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>(); // Liste pour stocker les utilisateurs.
        String sql = "SELECT id, lastname, firstname, email, address, phone FROM users"; // SQL pour sélectionner tous les utilisateurs.
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) { // Exécution de la requête et récupération des résultats.
            while (rs.next()) { // Traitement de chaque ligne de résultat.
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("lastname"),
                    rs.getString("firstname"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("phone")
                ); // Création d'un nouvel utilisateur avec les données récupérées.
                users.add(user); // Ajout de l'utilisateur à la liste.
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users; // Retour de la liste des utilisateurs.
    }
}
