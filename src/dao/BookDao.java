package dao;

import model.Book;
import util.databaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDao {
    /**
     * Méthode pour récupérer tous les livres disponibles dans la base de données.
     * Cette méthode crée une liste de livres, puis utilise SQL pour interroger la base de données
     * et remplir cette liste avec les données récupérées (ISBN et stock de chaque livre).
     * @return Une liste contenant tous les livres récupérés de la base de données.
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT isbn, stock FROM books"; // Requête SQL pour récupérer ISBN et stock des livres
        try (Connection conn = databaseManager.getConnection(); // Obtention d'une connexion à la base de données
             PreparedStatement pstmt = conn.prepareStatement(sql); // Préparation de la requête
             ResultSet rs = pstmt.executeQuery()) { // Exécution de la requête et récupération des résultats
            while (rs.next()) { // Boucle sur chaque ligne résultante
                books.add(new Book(
                    rs.getString("isbn"), // Récupération de l'ISBN du livre
                    rs.getInt("stock") // Récupération du nombre de stocks disponibles
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des livres : " + e.getMessage());
        }
        return books;
    }

    /**
     * Méthode pour mettre à jour le stock d'un livre ou pour ajouter un nouveau livre si celui-ci n'existe pas déjà.
     * Trois requêtes SQL sont utilisées : une pour vérifier l'existence du livre, une autre pour mettre à jour
     * le stock si le livre existe, et une dernière pour insérer un nouveau livre si celui-ci n'existe pas.
     * @param isbn Le ISBN du livre à mettre à jour ou à ajouter.
     */
    public void updateOrAddBook(String isbn) {
        String sqlCheck = "SELECT stock FROM books WHERE isbn = ?"; // Requête SQL pour vérifier l'existence du livre
        String sqlUpdate = "UPDATE books SET stock = stock + 1 WHERE isbn = ?"; // Requête pour mettre à jour le stock
        String sqlInsert = "INSERT INTO books (isbn, stock) VALUES (?, 1)"; // Requête pour insérer un nouveau livre

        try (Connection conn = databaseManager.getConnection(); // Obtention de la connexion à la base de données
             PreparedStatement checkStmt = conn.prepareStatement(sqlCheck); // Préparation de la requête de vérification
             PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate); // Préparation de la requête de mise à jour
             PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)) { // Préparation de la requête d'insertion
            
            checkStmt.setString(1, isbn); // Assignation de l'ISBN à la requête de vérification
            ResultSet rs = checkStmt.executeQuery(); // Exécution de la requête de vérification
            
            if (rs.next()) { // Si le livre existe déjà
                updateStmt.setString(1, isbn); // Assignation de l'ISBN à la requête de mise à jour
                updateStmt.executeUpdate(); // Mise à jour du stock
            } else { // Si le livre n'existe pas
                insertStmt.setString(1, isbn); // Assignation de l'ISBN à la requête d'insertion
                insertStmt.executeUpdate(); // Insertion du nouveau livre
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour ou de l'ajout d'un livre : " + e.getMessage());
        }
    }

    /**
     * Méthode pour trouver un livre spécifique par son ISBN.
     * Cette méthode utilise une requête SQL pour récupérer un livre à partir de son ISBN.
     * Si trouvé, un objet livre est créé et retourné.
     * @param isbn L'ISBN du livre à trouver.
     * @return Le livre trouvé ou null si aucun livre correspondant n'a été trouvé.
     */
    public Book findBookByISBN(String isbn) {
        String sql = "SELECT isbn, stock FROM books WHERE isbn = ?"; // Requête SQL pour trouver le livre
        try (Connection conn = databaseManager.getConnection(); // Obtention de la connexion à la base de données
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Préparation de la requête
            pstmt.setString(1, isbn); // Assignation de l'ISBN à la requête
            ResultSet rs = pstmt.executeQuery(); // Exécution de la requête et récupération du résultat
            if (rs.next()) { // Si un livre est trouvé
                return new Book(
                        rs.getString("isbn"), // Création de l'objet livre avec ISBN
                        rs.getInt("stock") // et stock récupérés
                    );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du livre par ISBN : " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Méthode pour obtenir le nombre de prêts pour un livre spécifique sur les 30 derniers jours.
     * Utilise une requête SQL pour compter le nombre de prêts d'un livre donné.
     * @param isbn L'ISBN du livre dont on veut connaître le nombre de prêts.
     * @return Le nombre de prêts du livre sur les 30 derniers jours.
     */
    public int getLoansCountForISBN(String isbn) {
        String sql = "SELECT COUNT(*) AS count FROM loans WHERE book_isbn = ? AND date_loaned >= CURDATE() - INTERVAL 30 DAY"; // Requête pour compter les prêts
        try (Connection conn = databaseManager.getConnection(); // Obtention de la connexion
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Préparation de la requête
            pstmt.setString(1, isbn); // Assignation de l'ISBN à la requête
            ResultSet rs = pstmt.executeQuery(); // Exécution de la requête et récupération du résultat
            if (rs.next()) { // Si le résultat est disponible
                return rs.getInt("count"); // Retourne le nombre de prêts
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du nombre de prêts pour l'ISBN : " + e.getMessage());
        }
        return 0;
    }
}
