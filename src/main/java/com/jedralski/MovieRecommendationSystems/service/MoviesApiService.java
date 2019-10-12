package com.jedralski.MovieRecommendationSystems.service;

import com.jedralski.MovieRecommendationSystems.exception.ApiException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;

import java.util.LinkedHashMap;
import java.util.Map;

public interface MoviesApiService {

    Long findMovieIdByTitle(String title) throws ApiException;

    MovieRatings findMovieDetailsByMovieId(long movieId) throws ApiException;

    int findMainActorByMovieId(long movieId) throws ApiException;

    String findMovieTitleById(long movieId) throws ApiException;

    Map<Integer, String> findMovieTitlesByDetails(LinkedHashMap<Long, Double> genreRatingSorted, LinkedHashMap<Long, Double> productionCompanyRatingSorted, LinkedHashMap<Long, Double> mainActorRatingSorted) throws ApiException;
}
