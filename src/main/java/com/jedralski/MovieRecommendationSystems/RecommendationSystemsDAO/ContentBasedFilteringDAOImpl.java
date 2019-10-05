package com.jedralski.MovieRecommendationSystems.RecommendationSystemsDAO;

import org.springframework.stereotype.Repository;

@Repository
public class ContentBasedFilteringDAOImpl implements ContentBasedFilteringDAO{

    public String message(){
        return "Content Based Filtering";
    }
}
