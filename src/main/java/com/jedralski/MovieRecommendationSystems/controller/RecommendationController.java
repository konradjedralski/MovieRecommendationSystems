package com.jedralski.MovieRecommendationSystems.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jedralski.MovieRecommendationSystems.RecommendationSystemsService.CollaborativeFilteringService;
import com.jedralski.MovieRecommendationSystems.RecommendationSystemsService.ContentBasedFilteringService;
import com.jedralski.MovieRecommendationSystems.RecommendationSystemsService.HybridMethodService;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import com.jedralski.MovieRecommendationSystems.model.Neighbour;
import com.jedralski.MovieRecommendationSystems.model.User;
import com.jedralski.MovieRecommendationSystems.service.MovieRatingsService;
import com.jedralski.MovieRecommendationSystems.service.MoviesApiService;
import com.jedralski.MovieRecommendationSystems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/recommendation")
public class RecommendationController {

    @Autowired
    UserService userService;
    @Autowired
    MovieRatingsService movieRatingsService;
    @Autowired
    MoviesApiService moviesApiService;
    @Autowired
    CollaborativeFilteringService collaborativeFilteringService;
    @Autowired
    ContentBasedFilteringService contentBasedFilteringService;
    @Autowired
    HybridMethodService hybridMethodService;

    @RequestMapping("")
    public String addRating() {
        return "recommendation";
    }

    @PostMapping
    public String addRatingPost(@RequestParam("username") String username, @RequestParam(value = "action", required = true) String action, Model model) throws DatabaseException {
        User user = userService.getUserDataByUsername(username);
        if (user == null) {
            model.addAttribute("checkStatus", 0);
            return "recommendation";
        }
        switch (action) {
            case "collaborativeFiltering":
                Long userId = userService.getUserDataByUsername(username).getUserId();
                long startTime = System.currentTimeMillis();
                List<MovieRatings> userRatingsList = collaborativeFilteringService.userRatingsList(userId);
                List<Neighbour> neighbour = collaborativeFilteringService.getNeighbour(userRatingsList, userId);
                List<Neighbour> neighbourDistance = collaborativeFilteringService.getNeighbourDistance(userRatingsList, neighbour);
                HashMap<Long, Double> avgGenreRating = collaborativeFilteringService.avgGenreRating(userId);
                HashMap<String, Double> avgProductionCountriesRating = collaborativeFilteringService.avgProductionCountriesRating(userId);
                HashMap<Long, Double> avgMainActorRating = collaborativeFilteringService.avgMainActorRating(userId);
                List<Long> wantSeeMovies = collaborativeFilteringService.getWantSeeMovies(userId);
                List<MovieRatings> moviesToRecommended = collaborativeFilteringService.moviesToRecommended(userRatingsList, neighbourDistance, wantSeeMovies, avgGenreRating, avgProductionCountriesRating, avgMainActorRating);
                long endTime = System.currentTimeMillis();
                long duration = (endTime - startTime);
                model.addAttribute("duration", duration);
                Map<Long, String> moviesToRecommendedTitles = Maps.newHashMap();
                int i = 0;
                for (MovieRatings movies : moviesToRecommended) {
                    moviesToRecommendedTitles.put(movies.getMovieId(), moviesApiService.findMovieTitleById(movies.getMovieId()));
                }
                model.addAttribute("recommendationName", "Collaborative Filtering");
                model.addAttribute("moviesToRecommended", moviesToRecommendedTitles);
                break;
            case "contentBasedFiltering":
                //model.addAttribute("recommendationName", contentBasedFilteringService.message());
                break;
            case "hybrid":
                //model.addAttribute("recommendationName", hybridMethodService.message());
                break;
        }
        return "recommendation";
    }
}
