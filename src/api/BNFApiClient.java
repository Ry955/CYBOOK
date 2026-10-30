package api;

import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.Book;
import model.DetailedBook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import dao.BookDao;

// Définition de la classe client pour interagir avec la Bibliothèque Nationale de France (BNF)
public class BNFApiClient {

    // Création d'une instance de BookDao pour la gestion des données des livres.
    private BookDao bookDao = new BookDao();
    // Création d'un client HTTP pour envoyer des requêtes à l'API.
    private final HttpClient client = HttpClient.newHttpClient();

    // Constructeur de la classe.
    public BNFApiClient() {
    }

    // Méthode pour récupérer les détails d'un livre par son ISBN.
    public Document fetchBookDetailsByISBN(String isbn) {
        String query = "bib.isbn adj \"" + isbn + "\"";  // Construction de la requête.
        return fetchDetails(query);  // Appel à la méthode qui envoie la requête.
    }

    // Méthode pour rechercher des livres avec des informations détaillées.
    public List<DetailedBook> searchBooks(String query) {
        Document doc = fetchDetails(query);  // Récupération du document XML de la réponse.
        if (doc != null) {
            return extractBooks(doc);  // Extraction des livres du document XML.
        }
        return new ArrayList<>();  // Retour d'une liste vide si aucune information n'est trouvée.
    }

    // Méthode privée pour envoyer la requête à l'API et obtenir le document XML de réponse.
    private Document fetchDetails(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());  // Encodage de la requête pour URL.
            String url = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=" + encodedQuery;

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/xml")
                .build();  // Construction de la requête HTTP.

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());  // Envoi de la requête.

            if (response.statusCode() == 200) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);  // Configuration nécessaire pour les documents XML avec espaces de noms.
                DocumentBuilder builder = factory.newDocumentBuilder();
                return builder.parse(new InputSource(new StringReader(response.body())));  // Parsing de la réponse en document XML.
            } else {
                System.err.println("Request failed: " + response.statusCode());  // Gestion des erreurs de requête.
            }
        } catch (Exception e) {
            e.printStackTrace();  // Affichage des erreurs.
        }
        return null;  // Retourne null en cas d'erreur.
    }

    // Méthode privée pour vérifier la disponibilité d'un livre.
    private String checkAvailability(String isbn) {
        Book book = bookDao.findBookByISBN(isbn);  // Recherche du livre dans la base de données.
        if (book != null) {
            int stock = book.getStock();  // Vérification du stock.
            return stock > 0 ? "In stock (" + stock + ")" : "Out of stock";  // Retour du statut de stock.
        }
        return "Not available";  // Livre non disponible si non trouvé.
    }

    // Méthode privée pour extraire des livres depuis un document XML.
    private List<DetailedBook> extractBooks(Document doc) {
        List<DetailedBook> books = new ArrayList<>();
        NodeList recordNodes = doc.getElementsByTagNameNS("*", "record");  // Extraction des noeuds "record".
        for (int i = 0; i < recordNodes.getLength(); i++) {
            Element record = (Element) recordNodes.item(i);
            // Extraction des informations spécifiques du livre.
            String isbn = extractField(record, "010", 'a');
            String title = extractField(record, "200", 'a');
            String author = extractField(record, "700", 'a');
            String publicationDate = extractField(record, "210", 'd');
            String edition = extractField(record, "205", 'a');
            String collection = extractField(record, "225", 'a');
            String availability = checkAvailability(isbn);  // Vérification de la disponibilité du livre.

            // Ajout du livre à la liste si le titre n'est pas vide.
            if (title != null && !title.isBlank()) {
                books.add(new DetailedBook(isbn, title, author, publicationDate, edition, collection, 0, availability));
            }
        }
        return books;  // Retour de la liste des livres.
    }

    // Méthode privée pour extraire une donnée spécifique depuis un champ UNIMARC.
    private String extractField(Element record, String tag, char subfieldCode) {
        NodeList dataFields = record.getElementsByTagNameNS("*", "datafield");  // Extraction des champs "datafield".
        for (int i = 0; i < dataFields.getLength(); i++) {
            Element field = (Element) dataFields.item(i);
            if (field.getAttribute("tag").equals(tag)) {
                NodeList subFields = field.getElementsByTagNameNS("*", "subfield");  // Extraction des sous-champs.
                for (int j = 0; j < subFields.getLength(); j++) {
                    Element subField = (Element) subFields.item(j);
                    if (subField.getAttribute("code").charAt(0) == subfieldCode) {
                        return subField.getTextContent().trim();  // Retour de la donnée textuelle.
                    }
                }
            }
        }
        return "";  // Retourne une chaîne vide si l'information n'est pas trouvée.
    }

    // Extraction de titre depuis un document XML.
    private String extractTitle(Document doc) {
        NodeList nodes = doc.getElementsByTagNameNS("*", "subfield");
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getParentNode().getAttributes().getNamedItem("tag").getNodeValue().equals("200")
                && nodes.item(i).getAttributes().getNamedItem("code").getNodeValue().equals("a")) {
                return nodes.item(i).getTextContent();  // Retour du contenu textuel.
            }
        }
        return null;  // Retourne null si aucun titre n'est trouvé.
    }

    // Extraction d'auteur depuis un document XML.
    private String extractAuthor(Document doc) {
        NodeList nodes = doc.getElementsByTagNameNS("*", "subfield");
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getParentNode().getAttributes().getNamedItem("tag").getNodeValue().equals("700")
                && nodes.item(i).getAttributes().getNamedItem("code").getNodeValue().equals("a")) {
                return nodes.item(i).getTextContent();  // Retour de l'auteur.
            }
        }
        return null;  // Retourne null si aucun auteur n'est trouvé.
    }

    // Méthode pour récupérer le titre d'un livre par ISBN.
    public String fetchTitle(String isbn) {
        Document bookData = fetchBookDetailsByISBN(isbn);  // Obtention des données du livre.
        if (bookData != null) {
            return extractTitle(bookData);  // Extraction et retour du titre.
        } else {
            return "Titre non disponible";  // Indication si les données du livre ne sont pas accessibles.
        }
    }

    // Méthode pour récupérer l'auteur d'un livre par ISBN.
    public String fetchAuthor(String isbn) {
        Document bookData = fetchBookDetailsByISBN(isbn);  // Obtention des données de l'auteur.
        if (bookData != null) {
            return extractAuthor(bookData);  // Extraction et retour de l'auteur.
        } else {
            return "Auteur non disponible";  // Indication si les données de l'auteur ne sont pas accessibles.
        }
    }

    // Méthode pour lire un fichier XML local.
    public Document readLocalXMLFile(String filePath) {
        try {
            File file = new File(filePath);  // Création d'un objet File.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);  // Configuration de la fabrique pour les documents avec espaces de noms.
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(file);  // Parsing du fichier et retour du document.
        } catch (Exception e) {
            e.printStackTrace();  // Affichage des erreurs.
        }
        return null;  // Retourne null en cas d'erreur lors du parsing.
    }
}
