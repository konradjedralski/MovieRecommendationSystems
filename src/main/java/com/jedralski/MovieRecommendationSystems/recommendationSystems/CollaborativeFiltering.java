package com.jedralski.MovieRecommendationSystems.recommendationSystems;

import com.jedralski.MovieRecommendationSystems.RecommendationSystemsService.CollaborativeFilteringService;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import com.jedralski.MovieRecommendationSystems.model.Neighbour;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class CollaborativeFiltering {

    @Autowired
    CollaborativeFilteringService collaborativeFilteringService;

    public List<MovieRatings> moviesToRecommended(Long userId) throws DatabaseException {
        List<MovieRatings> userRatingsList = collaborativeFilteringService.userRatingsList(userId);
        List<Neighbour> neighbour = collaborativeFilteringService.getNeighbour(userRatingsList, userId);
        List<Neighbour> neighbourDistance = collaborativeFilteringService.getNeighbourDistance(userRatingsList, neighbour);
        HashMap<Long, Double> avgGenreRating = collaborativeFilteringService.avgGenreRating(userId);
        HashMap<String, Double> avgProductionCountriesRating = collaborativeFilteringService.avgProductionCountriesRating(userId);
        HashMap<Long, Double> avgMainActorRating = collaborativeFilteringService.avgMainActorRating(userId);
        List<Long> wantSeeMovies = collaborativeFilteringService.getWantSeeMovies(userId);

        return collaborativeFilteringService.moviesToRecommended(userRatingsList, neighbourDistance, wantSeeMovies, avgGenreRating, avgProductionCountriesRating, avgMainActorRating);
    }
}
