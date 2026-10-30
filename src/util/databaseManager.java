// Déclaration du package 'util' qui pourrait contenir des classes utilitaires utilisées dans l'ensemble du projet.
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire pour la gestion des connexions à une base de données.
 * Cette classe fournit une méthode pour obtenir une connexion à la base de données MySQL.
 */
public class databaseManager {
	/**
     * Constructeur par défaut de la classe.
     * Il est vide car il n'y a pas de variables d'instance à initialiser spécifiquement pour cette classe.
     */
    public databaseManager() {
        // Ce constructeur est vide, principalement utilisé pour créer des instances de databaseManager si nécessaire.
    }
	
    // Bloc statique pour charger le pilote JDBC MySQL.
    static {
        try {
            // Tentative de chargement du pilote JDBC pour MySQL.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Affichage d'une erreur en cas d'échec du chargement du pilote.
            System.err.println("Failed to load JDBC driver");
            e.printStackTrace();
        }
    }

    /**
     * Méthode pour obtenir une connexion à la base de données MySQL.
     * @return Un objet Connection connecté à la base de données MySQL, ou null si la connexion échoue.
     */
    public static Connection getConnection() {
        try {
            // URL, utilisateur et mot de passe lus depuis l'environnement (valeurs par défaut pour un poste de développement local).
            String url = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/cybook");
            String user = System.getenv().getOrDefault("DB_USER", "root");
            String password = System.getenv().getOrDefault("DB_PASSWORD", "");
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            // Affichage de la trace de l'erreur en cas d'échec de la connexion.
            e.printStackTrace();
            return null;  // Retourne null si une erreur survient, indiquant l'échec de la connexion.
        }
    }
}
