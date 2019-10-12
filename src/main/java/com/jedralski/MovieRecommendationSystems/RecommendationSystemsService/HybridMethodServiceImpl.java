package com.jedralski.MovieRecommendationSystems.RecommendationSystemsService;

import com.jedralski.MovieRecommendationSystems.RecommendationSystemsDAO.HybridMethodDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HybridMethodServiceImpl implements HybridMethodService{

    @Autowired
    private HybridMethodDAO hybridMethodDAO;

    @Override
    public String message() {
        return hybridMethodDAO.message();
    }
}
