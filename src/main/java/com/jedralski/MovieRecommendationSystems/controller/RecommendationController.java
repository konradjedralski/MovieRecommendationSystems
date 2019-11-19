package com.jedralski.MovieRecommendationSystems.controller;

import com.google.common.collect.Maps;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import com.jedralski.MovieRecommendationSystems.model.User;
import com.jedralski.MovieRecommendationSystems.recommendationSystems.CollaborativeFiltering;
import com.jedralski.MovieRecommendationSystems.recommendationSystems.ContentBasedFiltering;
import com.jedralski.MovieRecommendationSystems.recommendationSystems.HybridMethod;
import com.jedralski.MovieRecommendationSystems.service.MovieRatingsService;
import com.jedralski.MovieRecommendationSystems.service.MoviesApiService;
import com.jedralski.MovieRecommendationSystems.service.UserService;
import com.jedralski.MovieRecommendationSystems.service.WantSeeService;
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
    private MovieRatingsService movieRatingsService;
    @Autowired
    private WantSeeService wantSeeService;
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
    public String addRatingPost(@RequestParam("username") String username, @RequestParam(value = "action", required = true) String action, Model model) throws DatabaseException, InterruptedException {
        User user = userService.getUserDataByUsername(username);
        if (user == null) {
            model.addAttribute("checkStatus", 0);
            return "recommendation";
        }
        switch (action) {
            case "collaborativeFiltering":
                long startTime1 = System.currentTimeMillis();
                Map<Long, String> moviesToRecommendedCollaborative = moviesToRecommendedCollaborative(user.getUserId());
                long endTime1 = System.currentTimeMillis();
                long duration1 = (endTime1 - startTime1);
                model.addAttribute("duration", duration1);
                model.addAttribute("recommendationName", "Collaborative Filtering");
                model.addAttribute("user", user.getUsername());
                model.addAttribute("moviesToRecommended", moviesToRecommendedCollaborative);
                break;
            case "contentBasedFiltering":
                long startTime2 = System.currentTimeMillis();
                LinkedHashMap<Long, String> moviesToRecommendedContentBased = moviesToRecommendedContentBased(user.getUserId());
                long endTime2 = System.currentTimeMillis();
                long duration2 = (endTime2 - startTime2);
                model.addAttribute("duration", duration2);
                model.addAttribute("recommendationName", "Content Based Filtering");
                model.addAttribute("user", user.getUsername());
                model.addAttribute("moviesToRecommended", moviesToRecommendedContentBased.entrySet().stream().limit(20)
                                                                                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new)));
                break;
            case "hybrid":
                long startTime3 = System.currentTimeMillis();
                Map<Long, String> moviesToRecommendedHybrid = moviesToRecommendedHybrid(user.getUserId());
                long endTime3 = System.currentTimeMillis();
                long duration3 = (endTime3 - startTime3 - 5000);
                model.addAttribute("duration", duration3);
                model.addAttribute("recommendationName", "Hybrid Filtering");
                model.addAttribute("user", user.getUsername());
                model.addAttribute("moviesToRecommended", moviesToRecommendedHybrid);
                break;
        }
        return "recommendation";
    }

    private Map<Long, String> moviesToRecommendedCollaborative(Long userId) throws DatabaseException {
        List<MovieRatings> moviesToRecommendedCollaborativeList = collaborativeFiltering.moviesToRecommended(userId);
        Map<Long, String> moviesToRecommendedCollaborative = Maps.newHashMap();
        for (MovieRatings movies : moviesToRecommendedCollaborativeList) {
            moviesToRecommendedCollaborative.put(movies.getMovieId(), moviesApiService.findMovieTitleById(movies.getMovieId()));
            if (moviesToRecommendedCollaborative.size() == 20) {
                break;
            }
        }
        return moviesToRecommendedCollaborative;
    }

    private LinkedHashMap<Long, String> moviesToRecommendedContentBased(Long userId) throws DatabaseException {
        LinkedHashMap<Long, Double> genreRatingSorted = contentBasedFiltering.sortGenres(userId);
        LinkedHashMap<Long, Double> productionCompanyRatingSorted = contentBasedFiltering.sortProductionCompanies(userId);
        LinkedHashMap<Long, Double> mainActorRatingSorted = contentBasedFiltering.sortActors(userId);
        List<Long> userRatingMovies = movieRatingsService.getMovieIdFromMovieRatings(userId);
        List<Long> userWantSeeMovies = wantSeeService.getMovieIdFromWantSee(userId);
        LinkedHashMap<Long, String> moviesToRecommendedContentBased = Maps.newLinkedHashMap();
        int maxSize = Math.max(genreRatingSorted.size(), Math.max(productionCompanyRatingSorted.size(), mainActorRatingSorted.size()));
        int limit = 3;
        for (int a = 0; a < maxSize; a++) {
            moviesToRecommendedContentBased = moviesApiService.findMovieTitlesByDetails(
                    genreRatingSorted.entrySet().stream().limit(limit)
                                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new)),
                    productionCompanyRatingSorted.entrySet().stream().limit(limit)
                                                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new)),
                    mainActorRatingSorted.entrySet().stream().limit(limit)
                                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new))
            );
            if (moviesToRecommendedContentBased.size() >= 20) {
                for (Long ratingMovieId : userRatingMovies) {
                    if (moviesToRecommendedContentBased.containsKey(ratingMovieId)) {
                        moviesToRecommendedContentBased.remove(ratingMovieId);
                    }
                }
                for (Long wantSeeMovieId : userWantSeeMovies) {
                    if (moviesToRecommendedContentBased.containsKey(wantSeeMovieId)) {
                        moviesToRecommendedContentBased.remove(wantSeeMovieId);
                    }
                }
                if (moviesToRecommendedContentBased.size() >= 20) {
                    break;
                }
            }
            limit++;
        }
        return moviesToRecommendedContentBased;
    }

    private Map<Long, String> moviesToRecommendedHybrid(Long userId) throws DatabaseException, InterruptedException {
        List<MovieRatings> moviesToRecommendedCollaborativeList = collaborativeFiltering.moviesToRecommended(userId);
        LinkedHashMap<Long, Double> genreRatingSorted = contentBasedFiltering.sortGenres(userId);
        LinkedHashMap<Long, Double> productionCompanyRatingSorted = contentBasedFiltering.sortProductionCompanies(userId);
        LinkedHashMap<Long, Double> mainActorRatingSorted = contentBasedFiltering.sortActors(userId);
        List<Long> userRatingMovies = movieRatingsService.getMovieIdFromMovieRatings(userId);
        List<Long> userWantSeeMovies = wantSeeService.getMovieIdFromWantSee(userId);
        LinkedHashMap<Long, String> moviesToRecommendedContentBased = Maps.newLinkedHashMap();
        int maxSize = Math.max(genreRatingSorted.size(), Math.max(productionCompanyRatingSorted.size(), mainActorRatingSorted.size()));
        int limit = 3;
        for (int a = 0; a < maxSize; a++) {
            moviesToRecommendedContentBased = moviesApiService.findMovieTitlesByDetails(
                    genreRatingSorted.entrySet().stream().limit(limit)
                                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new)),
                    productionCompanyRatingSorted.entrySet().stream().limit(limit)
                                                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new)),
                    mainActorRatingSorted.entrySet().stream().limit(limit)
                                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new))
            );
            if (moviesToRecommendedContentBased.size() >= 20) {
                for (Long ratingMovieId : userRatingMovies) {
                    if (moviesToRecommendedContentBased.containsKey(ratingMovieId)) {
                        moviesToRecommendedContentBased.remove(ratingMovieId);
                    }
                }
                for (Long wantSeeMovieId : userWantSeeMovies) {
                    if (moviesToRecommendedContentBased.containsKey(wantSeeMovieId)) {
                        moviesToRecommendedContentBased.remove(wantSeeMovieId);
                    }
                }
                if (moviesToRecommendedContentBased.size() >= 20) {
                    break;
                }
            }
            limit++;
        }
        List<MovieRatings> moviesToRecommendedContentBasedWithRatings = contentBasedFiltering.addRecommendationRating(moviesToRecommendedContentBased, genreRatingSorted, productionCompanyRatingSorted, mainActorRatingSorted);
        List<Long> moviesToRecommendedHybridList = hybridMethod.getHybridRecommendation(moviesToRecommendedCollaborativeList, moviesToRecommendedContentBasedWithRatings);
        Map<Long, String> moviesToRecommendedHybrid = Maps.newHashMap();

        Thread.sleep(5000);
        for (Long movie : moviesToRecommendedHybridList) {
            moviesToRecommendedHybrid.put(movie.longValue(), moviesApiService.findMovieTitleById(movie.longValue()));
        }
        return moviesToRecommendedHybrid;
    }
}
