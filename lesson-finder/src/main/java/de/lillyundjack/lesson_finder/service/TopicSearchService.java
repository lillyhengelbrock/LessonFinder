package de.lillyundjack.lesson_finder.service;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class TopicSearchService {

    private final String apiUrl = "https://lernen.lillyundjack.de/wp-json/ldlms/v1/sfwd-topic";
    private final String username = AuthenticationConstants.USERNAME;
    private final String password = AuthenticationConstants.PASSWORD;

    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    public String searchTopics(String searchTerm) {
        String url = apiUrl + "?search=" + searchTerm;

        HttpGet request = new HttpGet(url);
        String auth = username + ":" + password;
        byte[] encodedAuth = java.util.Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        request.setHeader(HttpHeaders.ACCEPT, "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return responseBody;
            } else {
                return "Failed to fetch data. HTTP status code: " + response.getStatusLine().getStatusCode();
            }
        } catch (Exception e) {
            return "Failed to fetch data. Exception: " + e.getMessage();
        }
    }
}
