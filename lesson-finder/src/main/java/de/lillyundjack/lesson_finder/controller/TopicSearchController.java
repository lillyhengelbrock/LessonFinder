package de.lillyundjack.lesson_finder.controller;

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
    public String searchTopics(@RequestParam String searchTerm) {
        return topicSearchService.searchTopics(searchTerm);
    }
}
