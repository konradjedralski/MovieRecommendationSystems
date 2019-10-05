package com.jedralski.MovieRecommendationSystems.service;

import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.exception.InputException;
import com.jedralski.MovieRecommendationSystems.model.User;

public interface UserService {

    boolean addUser (User user) throws InputException, DatabaseException;

    User getUserDataByUsername (String username) throws DatabaseException;
}
