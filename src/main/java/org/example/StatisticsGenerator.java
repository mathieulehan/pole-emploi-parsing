package org.example;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static com.mongodb.client.model.Filters.eq;

public class StatisticsGenerator {

    private static MongoDatabase database;

    private static final String uri = "mongodb://localhost:27017/?maxPoolSize=20&w=majority";

    public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // update aggregation collections
        updateTypeContrat();
        updateEntreprise();
        /**
         * pas trouvé d'info pays (à moins de se baser sur les départements ?)
         */
        // updatePays();

        // then displays stats in console
        printStatisticsForCollection("typeContrat");
        printStatisticsForCollection("entreprises");
        // printStatisticsForCollection("pays");

        // TODO: un mongo export vers du csv par exemple
    }

    private static void printStatisticsForCollection(String collectionStr) {
        System.out.println("Printing all documents of collection : " + collectionStr);
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("hellowork");
            MongoCollection<Document> collection = database.getCollection(collectionStr);
            //Retrieving the documents
            FindIterable<Document> iterDoc = collection.find();
            Iterator it = iterDoc.iterator();
            while (it.hasNext()) {
                System.out.println(it.next());
            }
        }
    }

    private static void updateTypeContrat() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("hellowork");
            MongoCollection<Document> collection = database.getCollection("offres");
            ArrayList<Document> addedTypeContrats = collection.aggregate(Arrays.asList(new Document("$group",
                            new Document("_id", "$typeContrat")
                                    .append("count",
                                            new Document("$sum", 1L))),
                    new Document("$merge",
                            new Document("into", "typeContrat")
                                    .append("on", "_id")
                                    .append("whenMatched", "keepExisting")
                                    .append("whenNotMatched", "insert")))).into(new ArrayList<>());;
            System.out.println("Added " + addedTypeContrats.size() + " typeContrat statistics.");
        }
    }

    private static void updateEntreprise() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("hellowork");
            MongoCollection<Document> collection = database.getCollection("offres");
            ArrayList<Document> addedTypeContrats = collection.aggregate(Arrays.asList(new Document("$match",
                            new Document("entreprise.nom",
                                    new Document("$exists", true))),
                    new Document("$group",
                            new Document("_id", "$entreprise.nom")
                                    .append("count",
                                            new Document("$sum", 1L))),
                    new Document("$merge",
                            new Document("into", "entreprises")
                                    .append("on", "_id")
                                    .append("whenMatched", "keepExisting")
                                    .append("whenNotMatched", "insert")))).into(new ArrayList<>());;
            System.out.println("Added " + addedTypeContrats.size() + " typeContrat statistics.");
        }
    }

}