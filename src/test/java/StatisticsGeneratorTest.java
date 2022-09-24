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
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class StatisticsGeneratorTest {

    @Test
    public void testEntreprisesStatistics() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        String[] args = null;
        OffreCollector.main(new HashSet<>(List.of()));
        String uri = "mongodb://localhost:27017/?maxPoolSize=20&w=majority";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("hellowork");
            MongoCollection<Document> collection = database.getCollection("entreprises");
            //Retrieving the documents
            ArrayList<Document> collectedOffers = collection.find().into(new ArrayList<>());
            assertNotEquals(0, collectedOffers.size());
        }
    }


    @Test
    public void testHelloworkTypeContrat() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        String[] args = null;
        OffreCollector.main(new HashSet<>(List.of()));
        String uri = "mongodb://localhost:27017/?maxPoolSize=20&w=majority";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("hellowork");
            MongoCollection<Document> collection = database.getCollection("typeContrat");
            //Retrieving the documents
            ArrayList<Document> collectedOffers = collection.find().into(new ArrayList<>());
            assertNotEquals(0, collectedOffers.size());
        }
    }

}
