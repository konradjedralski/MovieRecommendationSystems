package com.jedralski.MovieRecommendationSystems.service;

import com.jedralski.MovieRecommendationSystems.dao.MoviesApiDAO;
import com.jedralski.MovieRecommendationSystems.exception.ApiException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MoviesApiServiceImpl implements MoviesApiService {

    @Autowired
    MoviesApiDAO moviesApiDAO;

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
}
