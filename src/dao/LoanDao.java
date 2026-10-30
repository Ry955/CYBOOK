package dao;

import model.Loan;
import model.User;
import model.Book;
import util.databaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import api.BNFApiClient;

public class LoanDao {
    // Instance du client API pour récupérer des informations supplémentaires
    private BNFApiClient apiClient = new BNFApiClient();

    /**
     * Méthode pour récupérer tous les prêts de la base de données.
     * @return une liste de tous les prêts.
     */
    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        // Requête SQL pour sélectionner tous les prêts avec les informations de l'utilisateur et du livre
        String sql = "SELECT l.id, l.loanDate, l.dueDate, u.*, b.* FROM loans l " +
                     "JOIN users u ON l.userId = u.id " +
                     "JOIN books b ON l.bookId = b.id";
        // Utilisation d'un try-with-resources pour assurer la fermeture des ressources
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            // Parcourir les résultats de la requête
            while (rs.next()) {
                // Création d'un objet User à partir des résultats de la requête
                User user = new User(
                    rs.getInt("u.id"),
                    rs.getString("u.lastName"),
                    rs.getString("u.firstName"),
                    rs.getString("u.email"),
                    rs.getString("address"),
                    rs.getString("phone")
                );
                // Création d'un objet Book à partir des résultats de la requête
                Book book = new Book(
                        rs.getString("isbn"),
                        rs.getInt("stock")
                    );
                // Création d'un objet Loan à partir des résultats de la requête
                Loan loan = new Loan(
                    rs.getInt("l.id"),
                    user,
                    book,
                    rs.getDate("l.loanDate"),
                    rs.getDate("l.dueDate"),
                    rs.getInt("returned")
                );
                // Ajout du prêt à la liste des prêts
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching loans: " + e.getMessage());
        }
        return loans;
    }

    /**
     * Méthode pour ajouter un nouveau prêt dans la base de données.
     * @param loan le prêt à ajouter.
     */
    public void addLoan(Loan loan) {
        // Requête SQL pour insérer un nouveau prêt
        String sqlLoan = "INSERT INTO loans (user_id, book_isbn, date_loaned, due_date) VALUES (?, ?, ?, ?)";
        // Requête SQL pour mettre à jour le stock du livre
        String sqlUpdateStock = "UPDATE books SET stock = stock - 1 WHERE isbn = ? AND stock > 0";

        // Utilisation d'un try-with-resources pour assurer la fermeture des ressources
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmtLoan = conn.prepareStatement(sqlLoan);
             PreparedStatement pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock)) {

            // Ajouter le prêt
            pstmtLoan.setInt(1, loan.getUser().getId());
            pstmtLoan.setString(2, loan.getBook().getIsbn());
            pstmtLoan.setDate(3, new java.sql.Date(loan.getLoanDate().getTime()));
            pstmtLoan.setDate(4, new java.sql.Date(loan.getDueDate().getTime()));
            pstmtLoan.executeUpdate();

            // Décrémenter le stock du livre
            pstmtUpdateStock.setString(1, loan.getBook().getIsbn());
            int affectedRows = pstmtUpdateStock.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("No stock to decrement for book with ID: " + loan.getBook().getIsbn());
            }
        } catch (SQLException e) {
            System.err.println("Error adding loan and updating stock: " + e.getMessage());
        }
    }

    /**
     * Méthode pour supprimer un prêt de la base de données.
     * @param loanId l'ID du prêt à supprimer.
     */
    public void deleteLoan(int loanId) {
        // Requête SQL pour supprimer un prêt
        String sql = "DELETE FROM loans WHERE id = ?";
        // Utilisation d'un try-with-resources pour assurer la fermeture des ressources
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Définir l'ID du prêt à supprimer
            pstmt.setInt(1, loanId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting loan: " + e.getMessage());
        }
    }

    /**
     * Méthode pour obtenir la liste des livres empruntés par un utilisateur spécifique.
     * @param userId l'ID de l'utilisateur.
     * @return une liste de livres empruntés par l'utilisateur.
     */
    public List<Book> getBorrowedBooksByUser(int userId) {
        List<Book> borrowedBooks = new ArrayList<>();
        // Requête SQL pour sélectionner les livres empruntés par un utilisateur
        String sql = "SELECT b.isbn, b.stock FROM loans l JOIN books b ON l.book_isbn = b.isbn WHERE l.user_id = ? AND l.returned = 0";
        // Utilisation d'un try-with-resources pour assurer la fermeture des ressources
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Définir l'ID de l'utilisateur
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            // Parcourir les résultats de la requête
            while (rs.next()) {
                borrowedBooks.add(new Book(rs.getString("isbn"), rs.getInt("stock")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching books borrowed by user: " + e.getMessage());
        }
        return borrowedBooks;
    }

    /**
     * Méthode pour obtenir la liste des utilisateurs ayant des prêts en cours.
     * @return une liste d'utilisateurs ayant des prêts en cours.
     */
    public List<User> getUsersWithCurrentLoans() {
        List<User> users = new ArrayList<>();
        // Requête SQL pour sélectionner les utilisateurs ayant des prêts en cours
        String sql = "SELECT DISTINCT u.* FROM users u JOIN loans l ON u.id = l.user_id WHERE l.returned = 0";
        // Utilisation d'un try-with-resources pour assurer la fermeture des ressources
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            // Parcourir les résultats de la requête
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("lastName"), rs.getString("firstName"), rs.getString("email"), rs.getString("address"), rs.getString("phone")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users with current loans: " + e.getMessage());
        }
        return users;
    }

    /**
     * Méthode pour obtenir la liste des prêts en retard.
     * @return une liste de prêts en retard.
     */
    public List<Loan> getLateLoans() {
        List<Loan> loans = new ArrayList<>();
        // Requête SQL pour sélectionner les prêts en retard
        String sql = "SELECT l.id as loanId, l.date_loaned, l.due_date, l.returned, " +
                     "u.id as userId, u.firstName, u.lastName, u.email, u.address, u.phone, " +
                     "b.isbn as bookIsbn, b.stock " +
                     "FROM loans l " +
                     "JOIN users u ON l.user_id = u.id " +
                     "JOIN books b ON l.book_isbn = b.isbn " +
                     "WHERE l.due_date < CURDATE() AND l.returned = 0";
        // Utilisation d'un try-with-resources pour assurer la fermeture des ressources
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            // Parcourir les résultats de la requête
            while (rs.next()) {
                // Création d'un objet User à partir des résultats de la requête
                User user = new User(
                    rs.getInt("userId"),
                    rs.getString("lastName"),
                    rs.getString("firstName"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("phone")
                );
                // Création d'un objet Book à partir des résultats de la requête
                Book book = new Book(
                    rs.getString("bookIsbn"),
                    rs.getInt("stock")
                );
                // Création d'un objet Loan à partir des résultats de la requête
                Loan loan = new Loan(
                    rs.getInt("loanId"),
                    user,
                    book,
                    rs.getDate("date_loaned"),
                    rs.getDate("due_date"),
                    rs.getInt("returned")
                );
                // Ajout du prêt en retard à la liste des prêts en retard
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching late loans: " + e.getMessage());
        }
        return loans;
    }

    /**
     * Méthode pour compter le nombre de prêts actifs pour un utilisateur spécifique.
     * @param userId l'ID de l'utilisateur.
     * @return le nombre de prêts actifs.
     */
    public int countActiveLoansByUser(int userId) {
        // Requête SQL pour compter le nombre de prêts actifs pour un utilisateur
        String sql = "SELECT COUNT(*) FROM loans WHERE user_id = ? AND returned = 0";
        // Utilisation d'un try-with-resources pour assurer la fermeture des ressources
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Définir l'ID de l'utilisateur
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Retourner le nombre de prêts actifs
            }
        } catch (SQLException e) {
            System.err.println("Error counting active loans: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Méthode pour compter le nombre de prêts en retard.
     * @return le nombre de prêts en retard.
     */
    public int countOverdueLoans() {
        int count = 0;
        // Requête SQL pour compter le nombre de prêts en retard
        String sql = "SELECT COUNT(*) FROM loans WHERE due_date < CURRENT_DATE AND returned=0";
        // Utilisation d'un try-with-resources pour assurer la fermeture des ressources
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1); // Retourner le nombre de prêts en retard
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Méthode pour retourner un prêt en mettant à jour l'état retourné.
     * @param userId l'ID de l'utilisateur.
     * @param isbn l'ISBN du livre.
     * @return true si le prêt a été retourné avec succès, false sinon.
     */
    public boolean returnLoan(int userId, String isbn) {
        // Requête SQL pour mettre à jour l'état retourné du prêt
        String sqlReturnLoan = "UPDATE loans SET returned = 1 "
                             + "WHERE user_id = ? AND book_isbn = ? AND returned = 0 "
                             + "ORDER BY due_date DESC "
                             + "LIMIT 1";

        // Requête SQL pour mettre à jour le stock du livre
        String sqlUpdateStock = "UPDATE books SET stock = stock + 1 WHERE isbn = ?";

        // Utilisation d'un try-with-resources pour assurer la fermeture des ressources
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmtReturnLoan = conn.prepareStatement(sqlReturnLoan);
             PreparedStatement pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock)) {

            // Marquer le prêt comme retourné
            pstmtReturnLoan.setInt(1, userId);
            pstmtReturnLoan.setString(2, isbn);
            int affectedRowsLoan = pstmtReturnLoan.executeUpdate();

            if (affectedRowsLoan > 0) {
                // Incrémenter le stock du livre
                pstmtUpdateStock.setString(1, isbn);
                int affectedRowsStock = pstmtUpdateStock.executeUpdate();
                if (affectedRowsStock > 0) {
                    return true;
                } else {
                    System.err.println("Erreur lors de l'incrémentation du stock pour le livre avec l'ISBN: " + isbn);
                    return false;
                }
            } else {
                System.err.println("Aucun prêt trouvé à retourner pour l'utilisateur avec ID: " + userId + " et le livre avec l'ISBN: " + isbn);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du retour du livre et de la mise à jour du stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Méthode pour récupérer les livres les plus empruntés des 30 derniers jours.
     * @return une liste des livres les plus empruntés.
     */
    public List<Book> fetchTopBooks() {
        List<Book> topBooks = new ArrayList<>();
        // Requête SQL pour sélectionner les livres les plus empruntés des 30 derniers jours
        String sql = "SELECT book_isbn, COUNT(*) AS count FROM loans "
                   + "WHERE date_loaned >= CURDATE() - INTERVAL 30 DAY "
                   + "GROUP BY book_isbn ORDER BY count DESC LIMIT 3";

        // Utilisation d'un try-with-resources pour assurer la fermeture des ressources
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            // Parcourir les résultats de la requête
            while (rs.next()) {
                String isbn = rs.getString("book_isbn");
                int stock = fetchStockForISBN(isbn); // Assurez-vous que cette méthode est correctement implémentée
                int loansCount = rs.getInt("count");
                Book book = new Book(isbn, stock);
                book.setLoansCount(loansCount); // Définir le nombre de prêts
                if (book.getTitle() != null && book.getAuthor() != null) {
                    topBooks.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching top borrowed books from the last 30 days: " + e.getMessage());
        }
        return topBooks;
    }

    /**
     * Récupérer le stock de la base de données pour un ISBN donné.
     * @param isbn l'ISBN du livre.
     * @return le stock du livre.
     */
    private int fetchStockForISBN(String isbn) {
        // Requête SQL pour sélectionner le stock d'un livre par son ISBN
        String sql = "SELECT stock FROM books WHERE isbn = ?";
        // Utilisation d'un try-with-resources pour assurer la fermeture des ressources
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Définir l'ISBN du livre
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching stock for ISBN: " + isbn + " - " + e.getMessage());
        }
        return 0; // Retourner un stock par défaut de 0 si non trouvé ou erreur
    }

    /**
     * Méthode pour obtenir la liste des prêts par un utilisateur spécifique.
     * @param userId l'ID de l'utilisateur.
     * @return une liste de prêts par l'utilisateur.
     */
    public List<Loan> getLoansByUser(int userId) {
        List<Loan> loans = new ArrayList<>();
        // Requête SQL pour sélectionner les prêts d'un utilisateur
        String sql = "SELECT l.id, l.book_isbn, l.date_loaned, l.due_date, l.returned, b.isbn, b.stock, u.lastName, u.firstName, u.email, u.address, u.phone " +
                     "FROM loans l " +
                     "JOIN books b ON l.book_isbn = b.isbn " +
                     "JOIN users u ON l.user_id = u.id " +
                     "WHERE l.user_id = ?";
        // Utilisation d'un try-with-resources pour assurer la fermeture des ressources
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Définir l'ID de l'utilisateur
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            // Parcourir les résultats de la requête
            while (rs.next()) {
                User user = new User(userId, rs.getString("lastName"), rs.getString("firstName"), rs.getString("email"), rs.getString("address"),rs.getString("phone"));
                Book book = new Book(rs.getString("isbn"), rs.getInt("stock"));
                int returned = rs.getInt("returned");
                Loan loan = new Loan(rs.getInt("id"), user, book, rs.getDate("date_loaned"), rs.getDate("due_date"), returned);
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching loans for user: " + e.getMessage());
        }
        return loans;
    }
}
