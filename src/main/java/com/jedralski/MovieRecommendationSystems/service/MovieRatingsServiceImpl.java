package com.jedralski.MovieRecommendationSystems.service;

import com.jedralski.MovieRecommendationSystems.dao.MovieRatingsDAO;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.exception.InputException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieRatingsServiceImpl implements MovieRatingsService {

    @Autowired
    private MovieRatingsDAO movieRatingsDAO;

    @Override
    public boolean addMovieRating(MovieRatings movieRatings) throws DatabaseException, InputException {
        return movieRatingsDAO.addMovieRating(movieRatings);
    }

    @Override
    public List<Long> getMovieIdFromMovieRatings(Long userId) throws DatabaseException {
        return movieRatingsDAO.getMovieIdFromMovieRatings(userId);
    }
}
