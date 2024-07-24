package de.lillyundjack.lesson_finder.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TopicSearchService {

    private final String apiUrl = "https://lernen.lillyundjack.de/wp-json/ldlms/v1/sfwd-topic"; 
    private final String username = AuthenticationConstants.USERNAME;
    private final String password = AuthenticationConstants.PASSWORD;

    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String searchTopics(String searchTerm) {
        
        String url = apiUrl + "?search=" + searchTerm + "&per_page=20";

        HttpGet request = new HttpGet(url);
        String auth = username + ":" + password;
        byte[] encodedAuth = java.util.Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        request.setHeader(HttpHeaders.ACCEPT, "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JSONArray sortedJsonArray = sortJsonArrayByDate(responseBody);
                return parseInfo(sortedJsonArray.toString(), searchTerm);
            } else {
                throw new RuntimeException("Failed to fetch data. HTTP status code: " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Failed to fetch data. Exception: " + e.getMessage(), e);
        }
        
    }

    


    private String parseInfo(String jsonResponse, String searchTerm) {
        StringBuilder formattedInfo = new StringBuilder();
        Set<String> uniqueLinks = new HashSet<>();
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            for (JsonNode node : root) {
                String title = node.path("title").path("rendered").asText();
                String link = node.path("link").asText();
                String content = node.path("content").path("rendered").asText();

                String sentence = extractSentence(content, searchTerm);

                Document doc = Jsoup.parse(content);
                Elements links = doc.select("a[href]");
                Elements iframes = doc.select("iframe");

                StringBuilder linksHtml = new StringBuilder();
                for (Element linkElement : links) {
                    String href = linkElement.attr("href");
                    if (uniqueLinks.add(href) && href.contains(".pdf")) {
                        linksHtml.append("<p>pdf: <a href=\"").append(href).append("\" target=\"_blank\">").append(href).append("</a></p>");
                
                    }
                }
                StringBuilder videosHtml = new StringBuilder();
                for (Element iframe : iframes) {
                    String src = iframe.attr("src");
                    if (src.contains("youtube-nocookie.com")) {
                        videosHtml.append("<p>YouTube Video: <a href=\"").append(src).append("\" target=\"_blank\">Hier Schauen</a></p>\n");
                    }
                }

                formattedInfo.append("<p><b>Lektion: ").append(title).append("</b></p>")
                            .append(sentence).append(" (...)").append("<br>")
                            .append("<p>Link: <a href=\"").append(link).append("\" target=\"_blank\">").append(link).append("</a></p>")
                            .append("<div>").append(linksHtml).append(videosHtml).append("</div>")
                            .append("<br><br>");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON response. Exception: " + e.getMessage(), e);
        
        }
        return formattedInfo.toString();

        }


    private String extractSentence(String content, String searchTerm) {
        if (content == null || searchTerm == null) {
            return "Invalid input.";
        }
        String regex = "(?s)([^.!?]*?\\b" + Pattern.quote(searchTerm) + "\\b[^.!?]*[.!?])";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return ".";
    }
    private JSONArray sortJsonArrayByDate(String responseBody) {
        JSONArray jsonArray = new JSONArray(responseBody);
        List<JSONObject> jsonList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            jsonList.add(jsonArray.getJSONObject(i));
        }

        jsonList.sort(Comparator.comparing(lesson -> lesson.getString("date")));

        return new JSONArray(jsonList);
    }
    }