import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.OffreCollector;
import org.junit.jupiter.api.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class OffreCollectorTest {

    @Test
    public void testCollect() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        String[] args = null;
        OffreCollector.main(new HashSet<>(List.of()));
        String uri = "mongodb://localhost:27017/?maxPoolSize=20&w=majority";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("hellowork");
            MongoCollection<Document> collection = database.getCollection("offres");
            //Retrieving the documents
            ArrayList<Document> collectedOffers = collection.find().into(new ArrayList<>());
            assertNotEquals(0, collectedOffers.size());
        }
    }

    @Test
    public void testCollectRennes() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        String[] args = null;
        OffreCollector.main(new HashSet<>(Arrays.asList("35238")));
        String uri = "mongodb://localhost:27017/?maxPoolSize=20&w=majority";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("hellowork");
            MongoCollection<Document> collection = database.getCollection("offres");
            //Retrieving the documents
            ArrayList<Document> collectedOffers = collection.find(eq("lieuTravail.commune", "35238")).into(new ArrayList<>());
            assertNotEquals(0, collectedOffers.size());
        }
    }

    @Test
    public void testCollectBordeaux() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        String[] args = null;
        OffreCollector.main(new HashSet<>(Arrays.asList("33063")));
        String uri = "mongodb://localhost:27017/?maxPoolSize=20&w=majority";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("hellowork");
            MongoCollection<Document> collection = database.getCollection("offres");
            //Retrieving the documents
            ArrayList<Document> collectedOffers = collection.find(eq("lieuTravail.commune", "33063")).into(new ArrayList<>());
            assertNotEquals(0, collectedOffers.size());
        }
    }

    @Test
    public void testCollectParis() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        String[] args = null;
        OffreCollector.main(new HashSet<>(Arrays.asList("75101")));
        String uri = "mongodb://localhost:27017/?maxPoolSize=20&w=majority";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("hellowork");
            MongoCollection<Document> collection = database.getCollection("offres");
            //Retrieving the documents
            ArrayList<Document> collectedOffers = collection.find(eq("lieuTravail.commune", "75101")).into(new ArrayList<>());
            assertNotEquals(0, collectedOffers.size());
        }
    }

}
