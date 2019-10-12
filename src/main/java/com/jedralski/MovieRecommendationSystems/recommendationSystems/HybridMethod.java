package com.jedralski.MovieRecommendationSystems.recommendationSystems;

import com.jedralski.MovieRecommendationSystems.RecommendationSystemsService.HybridMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HybridMethod {

    @Autowired
    HybridMethodService hybridMethodService;

}
