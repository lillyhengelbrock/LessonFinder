package de.lillyundjack.lesson_finder.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.lillyundjack.lesson_finder.DataTransferObject;

@Service
public class TopicSearchService {

    private final String apiUrl = "https://lernen.lillyundjack.de/wp-json/ldlms/v1/sfwd-topic"; 
    private final String username = AuthenticationConstants.USERNAME;
    private final String password = AuthenticationConstants.PASSWORD;

    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<DataTransferObject> searchTopics(String searchTerm) {
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
                return parseCourseInfo(responseBody);
            } else {
                throw new RuntimeException("Failed to fetch data. HTTP status code: " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Failed to fetch data. Exception: " + e.getMessage(), e);
        }
        
    }
    


    private List<DataTransferObject> parseCourseInfo(String jsonResponse) {
        List<DataTransferObject> courseInfoList = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            for (JsonNode node : root) {
                JsonNode titleNode = node.path("title");
                String title = titleNode.path("rendered").asText();
                courseInfoList.add(new DataTransferObject(title));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON response. Exception: " + e.getMessage(), e);
        
        }
        return courseInfoList;

        }
    }