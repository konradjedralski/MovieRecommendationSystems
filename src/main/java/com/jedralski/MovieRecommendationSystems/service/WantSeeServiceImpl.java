package com.jedralski.MovieRecommendationSystems.service;

import com.jedralski.MovieRecommendationSystems.dao.WantSeeDAO;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.exception.InputException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WantSeeServiceImpl implements WantSeeService{

    @Autowired
    WantSeeDAO wantSeeDAO;

    @Override
    public boolean addWantSee(MovieRatings movieRatings) throws DatabaseException, InputException {
        return wantSeeDAO.addWantSee(movieRatings);
    }
}
