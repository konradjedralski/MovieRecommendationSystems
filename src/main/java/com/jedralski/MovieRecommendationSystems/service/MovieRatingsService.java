package com.jedralski.MovieRecommendationSystems.service;

import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.exception.InputException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;

public interface MovieRatingsService {

    boolean addMovieRating(MovieRatings movieRatings) throws DatabaseException, InputException;
}
