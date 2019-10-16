package com.jedralski.MovieRecommendationSystems.dao;

import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.exception.InputException;
import com.jedralski.MovieRecommendationSystems.model.User;

public interface UserDAO {

    boolean addUser(User user) throws InputException, DatabaseException;

    User getUserDataByUsername(String username) throws DatabaseException;
}
