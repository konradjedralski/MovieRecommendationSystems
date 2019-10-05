package com.jedralski.MovieRecommendationSystems.service;

import com.jedralski.MovieRecommendationSystems.exception.ApiException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;

public interface MoviesApiService {

    Long findMovieIdByTitle(String title) throws ApiException;

    MovieRatings findMovieDetailsByMovieId(long movieId) throws ApiException;

    int findMainActorByMovieId(long movieId) throws ApiException;

    String findMovieTitleById(long movieId) throws ApiException;
}
