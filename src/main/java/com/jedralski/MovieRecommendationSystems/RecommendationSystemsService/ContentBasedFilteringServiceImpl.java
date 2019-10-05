package com.jedralski.MovieRecommendationSystems.RecommendationSystemsService;

import com.jedralski.MovieRecommendationSystems.RecommendationSystemsDAO.ContentBasedFilteringDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentBasedFilteringServiceImpl implements ContentBasedFilteringService {

    @Autowired
    ContentBasedFilteringDAO contentBasedFilteringDAO;

    @Override
    public String message() {
        return contentBasedFilteringDAO.message();
    }
}
