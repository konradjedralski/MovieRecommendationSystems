package com.jedralski.MovieRecommendationSystems.RecommendationSystemsService;

import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import com.jedralski.MovieRecommendationSystems.model.Neighbour;

import java.util.HashMap;
import java.util.List;

public interface CollaborativeFilteringService {

    List<MovieRatings> userRatingsList(Long userId) throws DatabaseException;

    List<Neighbour> getNeighbour(List<MovieRatings> userRatingsList, Long userId) throws DatabaseException;

    List<Neighbour> getNeighbourDistance(List<MovieRatings> userRatingsList, List<Neighbour> neighborhood) throws DatabaseException;

    HashMap<Long, Double> avgGenreRating(Long userId) throws DatabaseException;

    HashMap<String, Double> avgProductionCountriesRating(Long userId) throws DatabaseException;

    HashMap<Long, Double> avgMainActorRating(Long userId) throws DatabaseException;

    List<Long> getWantSeeMovies(Long userId) throws DatabaseException;

    List<MovieRatings> moviesToRecommended(List<MovieRatings> userRatingsList, List<Neighbour> neighborhood, List<Long> wantSeeMovies, HashMap<Long, Double> avgGenreRating, HashMap<String, Double> avgProductionCountriesRating, HashMap<Long, Double> avgMainActorRating) throws DatabaseException;
}
