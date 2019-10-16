package com.jedralski.MovieRecommendationSystems.service;

import com.jedralski.MovieRecommendationSystems.dao.MoviesApiDAO;
import com.jedralski.MovieRecommendationSystems.exception.ApiException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class MoviesApiServiceImpl implements MoviesApiService {

    @Autowired
    private MoviesApiDAO moviesApiDAO;

    @Override
    public Long findMovieIdByTitle(String title) throws ApiException {
        return moviesApiDAO.findMovieIdByTitle(title);
    }

    @Override
    public MovieRatings findMovieDetailsByMovieId(long movieId) throws ApiException {
        return moviesApiDAO.findMovieDetailsByMovieId(movieId);
    }

    @Override
    public int findMainActorByMovieId(long movieId) throws ApiException {
        return moviesApiDAO.findMainActorByMovieId(movieId);
    }

    @Override
    public String findMovieTitleById(long movieId) throws ApiException {
        return moviesApiDAO.findMovieTitleById(movieId);
    }

    @Override
    public LinkedHashMap<Long, String> findMovieTitlesByDetails(LinkedHashMap<Long, Double> genreRatingSorted, LinkedHashMap<Long, Double> productionCompanyRatingSorted, LinkedHashMap<Long, Double> mainActorRatingSorted) throws ApiException {
        return moviesApiDAO.findMovieTitlesByDetails(genreRatingSorted, productionCompanyRatingSorted, mainActorRatingSorted);
    }
}
