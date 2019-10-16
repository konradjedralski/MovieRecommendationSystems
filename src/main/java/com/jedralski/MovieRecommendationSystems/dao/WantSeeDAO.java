package com.jedralski.MovieRecommendationSystems.dao;

import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.exception.InputException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;

import java.util.List;

public interface WantSeeDAO {

    boolean addWantSee(MovieRatings movieRatings) throws DatabaseException, InputException;

    List<Long> getMovieIdFromWantSee(Long userId) throws DatabaseException;
}
