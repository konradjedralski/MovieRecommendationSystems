package com.jedralski.MovieRecommendationSystems.dao;

import com.google.common.collect.Lists;
import com.jedralski.MovieRecommendationSystems.connection.DBConnector;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.exception.InputException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MovieRatingsDAOImpl implements MovieRatingsDAO {

    @Override
    public boolean addMovieRating(MovieRatings movieRatings) throws DatabaseException, InputException {
        Integer[] genresArray = movieRatings.getGenresIds().toArray(new Integer[movieRatings.getGenresIds().size()]);
        Integer[] companiesArray = movieRatings.getProductionCompanies().toArray(new Integer[movieRatings.getProductionCompanies().size()]);
        String[] countriesArray = movieRatings.getProductionCountries().toArray(new String[movieRatings.getProductionCountries().size()]);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBConnector.getConnection();
            String query = "INSERT INTO movie_ratings (movie_id, user_id, rate, user_age, genres_ids, production_countries_iso, main_actor_id, real, production_companies) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, movieRatings.getMovieId());
            preparedStatement.setLong(2, movieRatings.getUserId());
            preparedStatement.setInt(3, movieRatings.getRate());
            preparedStatement.setInt(4, movieRatings.getUserAge());
            preparedStatement.setArray(5, connection.createArrayOf("INTEGER", genresArray));
            preparedStatement.setArray(6, connection.createArrayOf("VARCHAR", countriesArray));
            preparedStatement.setLong(7, movieRatings.getMainActorId());
            preparedStatement.setBoolean(8, true);
            preparedStatement.setArray(9, connection.createArrayOf("INTEGER", companiesArray));
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

    @Override
    public List<Long> getMovieIdFromMovieRatings(Long userId) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnector.getConnection();
            String query = "SELECT movie_id FROM movie_ratings WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();
            List<Long> movieRatingList = Lists.newArrayList();
            if (resultSet == null) {
                throw new DatabaseException("Error returning result.");
            }
            while (resultSet.next()) {
                Long movieId = resultSet.getLong("movie_id");

                movieRatingList.add(movieId);
            }
            return movieRatingList;
        } catch (SQLException e) {
            throw new DatabaseException("Problem with ID: " + e);
        } finally {
            DBConnector.closeResultSet(resultSet);
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }
}
