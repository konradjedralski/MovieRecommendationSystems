package com.jedralski.MovieRecommendationSystems.RecommendationSystemsService;

import com.jedralski.MovieRecommendationSystems.RecommendationSystemsDAO.ContentBasedFilteringDAO;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ContentBasedFilteringServiceImpl implements ContentBasedFilteringService {

    @Autowired
    private ContentBasedFilteringDAO contentBasedFilteringDAO;

    @Override
    public HashMap<Long, Double> avgGenreRating(Long userId) throws DatabaseException {
        return contentBasedFilteringDAO.avgGenreRating(userId);
    }

    @Override
    public HashMap<Long, Double> avgProductionCompanyRating(Long userId) throws DatabaseException {
        return contentBasedFilteringDAO.avgProductionCompanyRating(userId);
    }

    @Override
    public HashMap<Long, Double> avgMainActorRating(Long userId) throws DatabaseException {
        return contentBasedFilteringDAO.avgMainActorRating(userId);
    }

    @Override
    public HashMap<Long, Double> avgGenreWantSee(Long userId) throws DatabaseException {
        return contentBasedFilteringDAO.avgGenreWantSee(userId);
    }

    @Override
    public HashMap<Long, Double> avgProductionCompanyWantSee(Long userId) throws DatabaseException {
        return contentBasedFilteringDAO.avgProductionCompanyWantSee(userId);
    }

    @Override
    public HashMap<Long, Double> avgMainActorWantSee(Long userId) throws DatabaseException {
        return contentBasedFilteringDAO.avgMainActorWantSee(userId);
    }
}
