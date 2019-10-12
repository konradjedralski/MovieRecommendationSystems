package com.jedralski.MovieRecommendationSystems.RecommendationSystemsDAO;

import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;

import java.util.HashMap;

public interface ContentBasedFilteringDAO {

    HashMap<Long, Double> avgGenreRating(Long userId) throws DatabaseException;

    HashMap<Long, Double> avgProductionCompanyRating(Long userId) throws DatabaseException;

    HashMap<Long, Double> avgMainActorRating(Long userId) throws DatabaseException;

    HashMap<Long, Double> avgGenreWantSee(Long userId) throws DatabaseException;

    HashMap<Long, Double> avgProductionCompanyWantSee(Long userId) throws DatabaseException;

    HashMap<Long, Double> avgMainActorWantSee(Long userId) throws DatabaseException;
}
