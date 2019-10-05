package com.jedralski.MovieRecommendationSystems.RecommendationSystemsDAO;

import org.springframework.stereotype.Repository;

@Repository
public class HybridMethodDAOImpl implements HybridMethodDAO{

    public String message(){
        return "Hybrid";
    }
}
