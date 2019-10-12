package com.jedralski.MovieRecommendationSystems.controller;

import com.google.common.collect.Maps;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import com.jedralski.MovieRecommendationSystems.model.User;
import com.jedralski.MovieRecommendationSystems.recommendationSystems.CollaborativeFiltering;
import com.jedralski.MovieRecommendationSystems.recommendationSystems.ContentBasedFiltering;
import com.jedralski.MovieRecommendationSystems.recommendationSystems.HybridMethod;
import com.jedralski.MovieRecommendationSystems.service.MoviesApiService;
import com.jedralski.MovieRecommendationSystems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/recommendation")
public class RecommendationController {

    @Autowired
    private UserService userService;
    @Autowired
    private MoviesApiService moviesApiService;
    @Autowired
    private CollaborativeFiltering collaborativeFiltering;
    @Autowired
    private ContentBasedFiltering contentBasedFiltering;
    @Autowired
    private HybridMethod hybridMethod;

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
                long startTime1 = System.currentTimeMillis();
                List<MovieRatings> moviesToRecommended = collaborativeFiltering.moviesToRecommended(user.getUserId());
                long endTime1 = System.currentTimeMillis();
                long duration1 = (endTime1 - startTime1);
                model.addAttribute("duration", duration1);
                Map<Long, String> moviesToRecommendedByIds = Maps.newHashMap();
                for (MovieRatings movies : moviesToRecommended) {
                    moviesToRecommendedByIds.put(movies.getMovieId(), moviesApiService.findMovieTitleById(movies.getMovieId()));
                    if (moviesToRecommendedByIds.size() == 20) {
                        break;
                    }
                }
                model.addAttribute("recommendationName", "Collaborative Filtering");
                model.addAttribute("moviesToRecommended", moviesToRecommendedByIds);
                break;
            case "contentBasedFiltering":
                long startTime2 = System.currentTimeMillis();
                LinkedHashMap<Long, Double> genreRatingSorted = contentBasedFiltering.sortGenres(user.getUserId());
                LinkedHashMap<Long, Double> productionCompanyRatingSorted = contentBasedFiltering.sortProductionCompanies(user.getUserId());
                LinkedHashMap<Long, Double> mainActorRatingSorted = contentBasedFiltering.sortActors(user.getUserId());
                long endTime2 = System.currentTimeMillis();
                long duration2 = (endTime2 - startTime2);
                model.addAttribute("duration", duration2);
                Map<Integer, String> moviesToRecommendedByData = Maps.newHashMap();
                int maxSize = Math.max(genreRatingSorted.size(), Math.max(productionCompanyRatingSorted.size(), mainActorRatingSorted.size()));
                int limit = 3;
                for (int a = 0; a < maxSize; a++) {
                    moviesToRecommendedByData = moviesApiService.findMovieTitlesByDetails(
                            genreRatingSorted.entrySet().stream().limit(limit)
                                             .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new)),
                            productionCompanyRatingSorted.entrySet().stream().limit(limit)
                                                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new)),
                            mainActorRatingSorted.entrySet().stream().limit(limit)
                                                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new))
                    );
                    if (moviesToRecommendedByData != null && moviesToRecommendedByData.size() == 20) {
                        break;
                    }
                    limit++;
                }
                model.addAttribute("recommendationName", "Content Based Filtering");
                model.addAttribute("moviesToRecommended", moviesToRecommendedByData);
                break;
            case "hybrid":

                break;
        }
        return "recommendation";
    }
}
