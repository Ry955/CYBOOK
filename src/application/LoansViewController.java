package application;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import api.BNFApiClient;
import dao.BookDao;
import dao.LoanDao;
import dao.UserDao;
import model.Book;
import model.Loan;
import model.User;

import java.util.Date;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe contrôleur pour gérer les opérations liées aux emprunts dans le système de gestion de bibliothèque.
 */
public class LoansViewController {

    // Constructeur par défaut
    /**
     * Constructeur par défaut pour LoansViewController.
     */
    public LoansViewController() {
    }

    // Annotations FXML pour les éléments de l'interface utilisateur
    @FXML private ComboBox<User> userComboBox; // ComboBox pour sélectionner un utilisateur pour emprunter un livre
    @FXML private ComboBox<Book> bookComboBox; // ComboBox pour sélectionner un livre à emprunter
    @FXML private ComboBox<User> returnUserComboBox; // ComboBox pour sélectionner un utilisateur pour retourner un livre
    @FXML private ComboBox<Book> returnBookComboBox; // ComboBox pour sélectionner un livre à retourner
    @FXML private ListView<String> lateLoansListView; // ListView pour afficher les emprunts en retard
    @FXML private Label loanResponseLabel; // Label pour afficher les messages de réponse des emprunts
    @FXML private Label returnResponseLabel; // Label pour afficher les messages de réponse des retours
    @FXML private Label overdueBooksCountLabel; // Label pour afficher le nombre de livres en retard

    private BNFApiClient apiClient = new BNFApiClient();
    private BookDao bookDao = new BookDao();
    private UserDao userDao = new UserDao();
    private LoanDao loanDao = new LoanDao();

    /**
     * Méthode d'initialisation pour charger les données initiales et configurer les écouteurs.
     */
    public void initialize() {
        loadUsers();
        loadBooks();
        setupComboBoxListeners();
        updateOverdueBooksCount();
    }

    /**
     * Configurer les écouteurs pour les sélections de ComboBox.
     */
    private void setupComboBoxListeners() {
        returnUserComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadBooksBorrowedBy(newSelection);
            }
        });
    }

    /**
     * Charger les livres empruntés par un utilisateur spécifique.
     *
     * @param user L'utilisateur dont les livres empruntés doivent être chargés
     */
    private void loadBooksBorrowedBy(User user) {
        List<Book> borrowedBooks = loanDao.getBorrowedBooksByUser(user.getId());
        returnBookComboBox.setItems(FXCollections.observableArrayList(borrowedBooks));
    }

    /**
     * Charger les utilisateurs depuis la base de données.
     */
    private void loadUsers() {
        List<User> users = userDao.getAllUsers();
        users.sort((u1, u2) -> {
            int lastNameCompare = u1.getLastName().compareToIgnoreCase(u2.getLastName());
            if (lastNameCompare == 0) {
                return u1.getFirstName().compareToIgnoreCase(u2.getFirstName());
            }
            return lastNameCompare;
        });
        ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
        userComboBox.setItems(userObservableList);
        returnUserComboBox.setItems(userObservableList);
        if (!users.isEmpty()) {
            userComboBox.getSelectionModel().selectLast();
            returnUserComboBox.getSelectionModel().selectLast();
        }
    }

    /**
     * Charger les livres depuis la base de données.
     */
    private void loadBooks() {
        List<Book> books = bookDao.getAllBooks();
        books = books.stream()
                     .filter(book -> book.getStock() > 0)
                     .sorted(Comparator.comparing(Book::getIsbn))
                     .collect(Collectors.toList());
        bookComboBox.setItems(FXCollections.observableArrayList(books));
        if (!books.isEmpty()) {
            bookComboBox.getSelectionModel().selectFirst();
        }
    }

    /**
     * Gérer l'action d'emprunter un livre à un utilisateur.
     */
    @FXML
    private void handleLoanAction() {
        User selectedUser = userComboBox.getSelectionModel().getSelectedItem();
        Book selectedBook = bookComboBox.getSelectionModel().getSelectedItem();

        // Vérifier que l'utilisateur et le livre sont sélectionnés
        if (selectedUser == null || selectedBook == null) {
            updateResponseLabel(loanResponseLabel, "Veuillez sélectionner à la fois un utilisateur et un livre.", false);
            return;
        }

        // Vérifier le nombre d'emprunts actifs pour l'utilisateur
        int activeLoansCount = loanDao.countActiveLoansByUser(selectedUser.getId());
        if (activeLoansCount >= 3) {
            updateResponseLabel(loanResponseLabel, "Échec de l'emprunt : L'utilisateur a déjà emprunté trois livres.", false);
            return;
        }

        // Utiliser un nouveau thread pour éviter de bloquer l'interface utilisateur pendant la requête HTTP
        new Thread(() -> {
            String title = apiClient.fetchTitle(selectedBook.getIsbn());
            // Mettre à jour l'interface utilisateur sur le thread de l'application JavaFX
            Platform.runLater(() -> {
                if (!"Titre inconnu".equals(title)) {
                    Loan newLoan = new Loan(0, selectedUser, selectedBook, new java.util.Date(), calculateDueDate(), 0);
                    loanDao.addLoan(newLoan);
                    updateResponseLabel(loanResponseLabel, "Emprunt effectué pour " + selectedUser.getFirstName() + " " + selectedUser.getLastName() + " du livre \"" + title + "\"", true);
                    loadBooks(); // Recharger la liste des livres pour refléter les changements de stock
                    loadBooksBorrowedBy(selectedUser); // Recharger la liste des livres empruntés par l'utilisateur
                    updateOverdueBooksCount(); // Mettre à jour le compteur de livres en retard
                } else {
                    updateResponseLabel(loanResponseLabel, "Échec de l'emprunt : Le titre du livre est inconnu.", false);
                }
            });
        }).start();
    }

    /**
     * Gérer l'action de retour d'un livre.
     */
    @FXML
    private void handleReturnAction() {
        User selectedUser = returnUserComboBox.getSelectionModel().getSelectedItem();
        Book selectedBook = returnBookComboBox.getSelectionModel().getSelectedItem();

        if (selectedUser != null && selectedBook != null) {
            boolean returned = loanDao.returnLoan(selectedUser.getId(), selectedBook.getIsbn());
            if (returned) {
                returnResponseLabel.setText("Le livre a été retourné avec succès.");
                loadBooks();  // Actualiser la liste des livres pour mettre à jour le stock
                loadUsers();  // Actualiser les utilisateurs si nécessaire
            }

            boolean isReturnSuccessfully = loanDao.returnLoan(selectedUser.getId(), selectedBook.getIsbn());
            if (isReturnSuccessfully) {
                updateResponseLabel(returnResponseLabel, "Le livre a été retourné avec succès.", true);
            }

        } else {
            returnResponseLabel.setText("Veuillez sélectionner un utilisateur et un livre.");
            updateResponseLabel(returnResponseLabel, "Veuillez sélectionner un utilisateur et un livre.", false);
        }
        updateOverdueBooksCount();
    }

    /**
     * Gérer l'action d'afficher les emprunts en retard.
     */
    @FXML
    private void handleShowLateLoans() {
        List<Loan> lateLoans = loanDao.getLateLoans();
        ObservableList<String> formattedLoans = FXCollections.observableArrayList();

        // Ajouter les titres des colonnes
        formattedLoans.add(String.format("%-10s %-25s %-15s %-50s %-15s %-15s", "ID EMPRUNT", "Emprunteur", "ISBN", "Titre", "Date de retour", "Jours de retard"));
        // Ajouter les données
        lateLoans.forEach(loan -> formattedLoans.add(String.format(
                "%-10d %-25s %-15s %-50s %-15s %-15d",
                loan.getId(),
                loan.getUser().getFirstName() + " " + loan.getUser().getLastName(),
                loan.getBook().getIsbn(),
                loan.getBook().getTitle(),
                loan.getDueDate().toString(),
                calculateDaysLate(loan.getDueDate()))
        ));
        lateLoansListView.setItems(formattedLoans);
    }

    /**
     * Calculer la date d'échéance pour un emprunt.
     *
     * @return La date d'échéance pour l'emprunt
     */
    private java.util.Date calculateDueDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.WEEK_OF_YEAR, 2);
        return calendar.getTime();
    }

    /**
     * Calculer le nombre de jours de retard d'un emprunt.
     *
     * @param dueDate La date d'échéance de l'emprunt
     * @return Le nombre de jours de retard de l'emprunt
     */
    /**
     * Calculer le nombre de jours de retard d'un emprunt.
     *
     * @param dueDate La date d'échéance de l'emprunt
     * @return Le nombre de jours de retard de l'emprunt
     */
    private int calculateDaysLate(Date dueDate) {
        java.util.Date currentDate = new java.util.Date();
        long diffInMillies = currentDate.getTime() - dueDate.getTime();
        long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);
        return (int) diffInDays;
    }

    /**
     * Mettre à jour le nombre de livres en retard.
     */
    public void updateOverdueBooksCount() {
        // Vérifier d'abord si le label est initialisé
        if (overdueBooksCountLabel == null) {
            System.out.println("Le 'overdueBooksCountLabel' n'est pas initialisé.");
            return;  // Sortir de la méthode si le label n'est pas disponible
        }

        int overdueBooksCount = loanDao.countOverdueLoans(); // Obtenir le nombre de prêts en retard
        overdueBooksCountLabel.setText(String.valueOf(overdueBooksCount) + " livre(s) en retard");
    }

    /**
     * Mettre à jour le label de réponse avec un message et un style.
     *
     * @param label Le label à mettre à jour
     * @param message Le message à afficher
     * @param isSuccess Indique si l'opération a réussi
     */
    private void updateResponseLabel(Label label, String message, boolean isSuccess) {
        label.setText(message);
        // Appliquer le style en fonction du résultat
        if (isSuccess) {
            label.setStyle("-fx-text-fill: green;");  // Vert si succès
        } else {
            label.setStyle("-fx-text-fill: red;");    // Rouge si échec
        }
    }
}

