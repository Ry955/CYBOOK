# CYBOOK

Application de bureau de gestion de bibliothèque, réalisée en JavaFX dans le cadre de la formation CY TECH. Projet réalisé en groupe de cinq personnes.

## Fonctionnalités

- Gestion des livres : ajout/mise à jour par ISBN, recherche, suivi du stock.
- Gestion des utilisateurs : ajout, modification, recherche (avec validation du nom, de l'email, de l'adresse et du téléphone).
- Gestion des emprunts : création d'un emprunt, retour, calcul des dates d'échéance et des retards.
- Recherche de livres via l'API publique SRU de la Bibliothèque nationale de France (BNF), pour compléter les informations d'un ouvrage à partir de son ISBN.

## Technologies

- Java (JavaFX pour l'interface, FXML pour les vues)
- JDBC avec MySQL pour la persistance des données
- `java.net.http.HttpClient` pour les appels à l'API BNF (SRU), avec parsing XML (DOM)

## Structure du projet

```
src/
  api/          BNFApiClient.java        appels à l'API SRU de la BNF
  application/  Main.java + contrôleurs  démarrage et logique des vues (FXML)
  dao/          BookDao, UserDao, LoanDao accès aux données (JDBC, requêtes préparées)
  model/        Book, User, Loan, DetailedBook  entités
  util/         databaseManager, UserValidator  connexion BDD et validations
  img/          ressources graphiques utilisées par l'interface
```

## Prérequis

- JDK 11 ou supérieur (le client API utilise `java.net.http.HttpClient`, introduit en Java 11)
- JavaFX SDK (à ajouter séparément : le projet n'embarque pas de gestionnaire de dépendances)
- MySQL Server, avec une base contenant les tables `books`, `users` et `loans`
- Le connecteur JDBC MySQL (`mysql-connector-j`)

Ce projet a été développé sous Eclipse, sans fichier de build (pas de `pom.xml` ni de `.classpath` fourni). Pour le lancer, JavaFX SDK et le connecteur MySQL doivent être ajoutés manuellement aux bibliothèques du projet, puis `application.Main` exécuté comme application JavaFX.

## Configuration de la base de données

La connexion est lue depuis des variables d'environnement, avec des valeurs par défaut pour un poste local :

| Variable | Défaut |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/cybook` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | *(vide)* |

## Limites connues

- Aucun fichier de build (Maven/Gradle) n'est inclus : les dépendances (JavaFX, connecteur MySQL) doivent être ajoutées manuellement.
- Aucun script SQL de création des tables n'est fourni dans ce dépôt.
- Aucun test automatisé.
- Le schéma de base de données (tables `books`, `users`, `loans`) n'est pas documenté au-delà de ce qui est déduit du code des DAO.
