package de.lillyundjack.lesson_finder.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.lillyundjack.lesson_finder.DataTransferObject;
import de.lillyundjack.lesson_finder.service.TopicSearchService;


@RestController
public class TopicSearchController {

    private final TopicSearchService topicSearchService;

    public TopicSearchController(TopicSearchService topicSearchService) {
        this.topicSearchService = topicSearchService;
    }

    @GetMapping("/search/topics")
    public List<DataTransferObject> searchTopics(@RequestParam String searchTerm) {
        return topicSearchService.searchTopics(searchTerm);
    }
}
