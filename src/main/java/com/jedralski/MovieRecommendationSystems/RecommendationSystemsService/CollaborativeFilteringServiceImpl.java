package com.jedralski.MovieRecommendationSystems.RecommendationSystemsService;

import com.jedralski.MovieRecommendationSystems.RecommendationSystemsDAO.CollaborativeFilteringDAO;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import com.jedralski.MovieRecommendationSystems.model.Neighbour;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class CollaborativeFilteringServiceImpl implements CollaborativeFilteringService {

    @Autowired
    private CollaborativeFilteringDAO collaborativeFilteringDAO;


    @Override
    public List<MovieRatings> userRatingsList(Long userId) throws DatabaseException {
        return collaborativeFilteringDAO.userRatingsList(userId);
    }

    @Override
    public List<Neighbour> getNeighbour(List<MovieRatings> userRatingsList, Long userId) throws DatabaseException {
        return collaborativeFilteringDAO.getNeighbour(userRatingsList, userId);
    }

    @Override
    public List<Neighbour> getNeighbourDistance(List<MovieRatings> userRatingsList, List<Neighbour> neighborhood) throws DatabaseException {
        return collaborativeFilteringDAO.getNeighbourDistance(userRatingsList, neighborhood);
    }

    @Override
    public HashMap<Long, Double> avgGenreRating(Long userId) throws DatabaseException {
        return collaborativeFilteringDAO.avgGenreRating(userId);
    }

    @Override
    public HashMap<String, Double> avgProductionCountriesRating(Long userId) throws DatabaseException {
        return collaborativeFilteringDAO.avgProductionCountriesRating(userId);
    }

    @Override
    public HashMap<Long, Double> avgMainActorRating(Long userId) throws DatabaseException {
        return collaborativeFilteringDAO.avgMainActorRating(userId);
    }

    @Override
    public List<Long> getWantSeeMovies(Long userId) throws DatabaseException {
        return collaborativeFilteringDAO.getWantSeeMovies(userId);
    }

    @Override
    public List<MovieRatings> moviesToRecommended(List<MovieRatings> userRatingsList, List<Neighbour> neighborhood, List<Long> wantSeeMovies, HashMap<Long, Double> avgGenreRating, HashMap<String, Double> avgProductionCountriesRating, HashMap<Long, Double> avgMainActorRating) throws DatabaseException {
        return collaborativeFilteringDAO.moviesToRecommended(userRatingsList, neighborhood, wantSeeMovies, avgGenreRating, avgProductionCountriesRating, avgMainActorRating);
    }
}
