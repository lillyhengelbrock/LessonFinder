package de.lillyundjack.lesson_finder.controller;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.lillyundjack.lesson_finder.service.TopicSearchService;


@RestController
public class TopicSearchController {

    private final TopicSearchService topicSearchService;

    public TopicSearchController(TopicSearchService topicSearchService) {
        this.topicSearchService = topicSearchService;
    }

    @GetMapping("/search/topics")
    public ResponseEntity<String> searchTopics(@RequestParam String searchTerm) {
        String response = topicSearchService.searchTopics(searchTerm);
        String decodedResponse = StringEscapeUtils.unescapeHtml4(response); 
        String htmlResponse = "<html><head><meta charset=\"UTF-8\"><style>"
                + "body { background-color: white; color: black; font-family: Arial, sans-serif; }"
                + "pre { white-space: pre-wrap; word-wrap: break-word; }"
                + "</style></head><body><pre>" + decodedResponse + "</pre></body></html>";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);  
        return ResponseEntity.ok().headers(headers).body(htmlResponse);
    }
}
