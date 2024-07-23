package de.lillyundjack.lesson_finder.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.lillyundjack.lesson_finder.service.TopicSearchService;

@Controller
public class TopicSearchController {

    private final TopicSearchService topicSearchService;

    public TopicSearchController(TopicSearchService topicSearchService) {
        this.topicSearchService = topicSearchService;

    }

    @GetMapping("/search")
    public String searchPage() {
        return "search"; // returns the search.html template
    }

    @GetMapping("/searchResults")
    public String searchResults(@RequestParam("searchTerm") String searchTerm, Model model) {
        String results = topicSearchService.searchTopics(searchTerm);
        model.addAttribute("results", results);
        return "searchResults"; // returns the searchResults.html template
    }
}
