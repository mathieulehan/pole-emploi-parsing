package org.example;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main {

    // http codes
    private static final String OK = "200";
    // pole emploi api
    private static String api_url_token = "https://entreprise.pole-emploi.fr/connexion/oauth2/access_token?realm=/partenaire";
    private static String api_url_offres = "https://api.emploi-store.fr/partenaire/offresdemploi/v2/offres/search";
    // get token param constants
    private static String realm = "api_offresdemploiv2 o2dsoffre";
    // get token body constants
    private static String grant_type = "client_credentials";
    private static String client_id = "PAR_testtechhellowork_0180d04e402f5d747c57cd650e437bee15748e40ec4ee33edd45de7f17dfddc3";
    private static String client_secret = "e6bf0976807d9a600e2faef41d9976af2e820729650bcb062aee244a12d95760";
    private static String scope = "api_offresdemploiv2 o2dsoffre";

    // bearer token
    private static String token;

    private static HttpHeaders headersGet;
    private static HttpHeaders headersPost;
    private static RestTemplate restTemplate = new RestTemplate();
    private static MongoDatabase database;

    public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // Init parameters
        initHeadersPost();
        // Init restTemplate
        customize(restTemplate);
        // first call to token
        try {
            token = getTokenFromApi();
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        String uri = "mongodb://localhost:27017/?maxPoolSize=20&w=majority";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            database = mongoClient.getDatabase("hellowork");
            try {
                System.out.println("Connected successfully to server.");

                HttpStatus latestHttpCodeReceived = null;
                int page = 0;
                while (!OK.equals(latestHttpCodeReceived)) {
                    initHeadersGet();
                    latestHttpCodeReceived = getOffres(token);
                }

            } catch (MongoException me) {
                System.err.println("An error occurred while attempting to run a command: " + me);
            }
        }
    }

    private static void initHeadersPost() {
        // parameters
        headersPost = new HttpHeaders();
        headersPost.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    private static void initHeadersGet() {
        // parameters
        headersGet = new HttpHeaders();
        headersGet.set("Authorization", "Bearer " + token);
    }

    private static void customize(RestTemplate restTemplate)
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(requestFactory);
    }

    private static String getTokenFromApi() throws IOException, URISyntaxException, InterruptedException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        // request body
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", grant_type);
        map.add("client_id", client_id);
        map.add("client_secret", client_secret);
        map.add("scope", scope);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headersPost);

        ResponseEntity<String> response = restTemplate.exchange(api_url_token, HttpMethod.POST, entity, String.class);
        DocumentContext jsonContext = JsonPath.parse(response.getBody());
        return jsonContext.read("$.access_token");
    }

    // while http 206 on continue d'appeler
    private static HttpStatus getOffres(String token) {
        // request body
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        // map.add("grant_type", grant_type);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headersGet);

        ResponseEntity<String> response = restTemplate.exchange(api_url_offres, HttpMethod.GET, entity, String.class);
        DocumentContext jsonContext = JsonPath.parse(response.getBody());
        JSONArray jsonArray = jsonContext.read("resultats");
        for (Object offre : jsonArray) {
            parseAndSaveOffre((Map) offre);
        }
        return response.getStatusCode();
    }

    private static void parseAndSaveOffre(Map offre) {
        MongoCollection<BasicDBObject> collection = database.getCollection("offres", BasicDBObject.class);
        String intitule = (String) offre.get("intitule");
        System.out.println("Saving offer : " + intitule);
        // set _id to allow updates
        offre.put("_id", offre.get("id"));
        offre.remove("id");
        collection.insertOne(new BasicDBObject(offre));
    }

}