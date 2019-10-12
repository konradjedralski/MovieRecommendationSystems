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
public class WantSeeDAOImpl implements WantSeeDAO {

    @Override
    public boolean addWantSee(MovieRatings movieRatings) throws DatabaseException, InputException {
        Integer[] genresArray = movieRatings.getGenresIds().toArray(new Integer[movieRatings.getGenresIds().size()]);
        String[] countriesArray = movieRatings.getProductionCountries().toArray(new String[movieRatings.getProductionCountries().size()]);
        Integer[] companiesArray = movieRatings.getProductionCompanies().toArray(new Integer[movieRatings.getProductionCompanies().size()]);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBConnector.getConnection();
            String query = "INSERT INTO want_see (movie_id, user_id, genres_ids, production_countries_iso, main_actor_id, production_companies) VALUES (?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, movieRatings.getMovieId());
            preparedStatement.setLong(2, movieRatings.getUserId());
            preparedStatement.setArray(3, connection.createArrayOf("INTEGER", genresArray));
            preparedStatement.setArray(4, connection.createArrayOf("VARCHAR", countriesArray));
            preparedStatement.setLong(5, movieRatings.getMainActorId());
            preparedStatement.setArray(6, connection.createArrayOf("INTEGER", companiesArray));
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
