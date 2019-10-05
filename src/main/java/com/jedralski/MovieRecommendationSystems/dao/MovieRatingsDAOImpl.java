package com.jedralski.MovieRecommendationSystems.dao;

import com.jedralski.MovieRecommendationSystems.connection.DBConnector;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.exception.InputException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class MovieRatingsDAOImpl implements MovieRatingsDAO {

    @Override
    public boolean addMovieRating(MovieRatings movieRatings) throws DatabaseException, InputException {
        Integer[] genresArray = movieRatings.getGenresIds().toArray(new Integer[movieRatings.getGenresIds().size()]);
        String[] countriesArray = movieRatings.getProductionCountries().toArray(new String[movieRatings.getProductionCountries().size()]);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBConnector.getConnection();
            String query = "INSERT INTO movie_ratings (movie_id, user_id, rate, user_age, genres_ids, production_countries_iso, main_actor_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, movieRatings.getMovieId());
            preparedStatement.setLong(2, movieRatings.getUserId());
            preparedStatement.setInt(3, movieRatings.getRate());
            preparedStatement.setInt(4, movieRatings.getUserAge());
            preparedStatement.setArray(5, connection.createArrayOf("INTEGER", genresArray));
            preparedStatement.setArray(6, connection.createArrayOf("VARCHAR", countriesArray));
            preparedStatement.setLong(7, movieRatings.getMainActorId());
            preparedStatement.executeUpdate();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (SQLException e) {
            return false;
        } finally {
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }
}
