package com.jedralski.MovieRecommendationSystems.dao;

import com.jedralski.MovieRecommendationSystems.exception.ApiException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;

public interface MoviesApiDAO {

    Long findMovieIdByTitle(String title) throws ApiException;

    MovieRatings findMovieDetailsByMovieId(long movieId) throws ApiException;

    int findMainActorByMovieId(long movieId) throws ApiException;

    String findMovieTitleById(long movieId) throws ApiException;
}
