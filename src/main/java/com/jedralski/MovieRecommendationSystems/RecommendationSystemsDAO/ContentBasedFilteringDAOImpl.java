package com.jedralski.MovieRecommendationSystems.RecommendationSystemsDAO;

import com.google.common.collect.Maps;
import com.jedralski.MovieRecommendationSystems.connection.DBConnector;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

@Repository
public class ContentBasedFilteringDAOImpl implements ContentBasedFilteringDAO {


    @Override
    public HashMap<Long, Double> avgGenreRating(Long userId) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnector.getConnection();
            String query = "SELECT UNNEST(genres_ids) AS genres_ids, AVG(rate) AS avg_rate FROM movie_ratings WHERE user_id = ? GROUP BY UNNEST(genres_ids)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();
            HashMap<Long, Double> avgGenreRateMap = Maps.newHashMap();
            if (resultSet == null) {
                throw new DatabaseException("Error returning result.");
            }
            while (resultSet.next()) {
                Long genreId = resultSet.getLong("genres_ids");
                double avgRating = resultSet.getDouble("avg_rate");

                avgGenreRateMap.put(genreId, avgRating);
            }
            return avgGenreRateMap;
        } catch (SQLException e) {
            throw new DatabaseException("Problem with ID: " + e);
        } finally {
            DBConnector.closeResultSet(resultSet);
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }

    @Override
    public HashMap<Long, Double> avgProductionCompanyRating(Long userId) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnector.getConnection();
            String query = "SELECT UNNEST(production_companies) AS production_companies, AVG(rate) AS avg_rate FROM movie_ratings WHERE user_id = ? GROUP BY UNNEST(production_companies)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();
            HashMap<Long, Double> avgProductionCompaniesRateMap = Maps.newHashMap();
            if (resultSet == null) {
                throw new DatabaseException("Error returning result.");
            }
            while (resultSet.next()) {
                Long companyId = resultSet.getLong("production_companies");
                double avgRating = resultSet.getDouble("avg_rate");

                avgProductionCompaniesRateMap.put(companyId, avgRating);
            }
            return avgProductionCompaniesRateMap;
        } catch (SQLException e) {
            throw new DatabaseException("Problem with ID: " + e);
        } finally {
            DBConnector.closeResultSet(resultSet);
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }

    @Override
    public HashMap<Long, Double> avgMainActorRating(Long userId) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnector.getConnection();
            String query = "SELECT main_actor_id, AVG(rate) AS avg_rate FROM movie_ratings WHERE user_id = ? GROUP BY main_actor_id";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();
            HashMap<Long, Double> avgActorRateMap = Maps.newHashMap();
            if (resultSet == null) {
                throw new DatabaseException("Error returning result.");
            }
            while (resultSet.next()) {
                Long actorId = resultSet.getLong("main_actor_id");
                double avgRating = resultSet.getDouble("avg_rate");

                avgActorRateMap.put(actorId, avgRating);
            }
            return avgActorRateMap;
        } catch (SQLException e) {
            throw new DatabaseException("Problem with ID: " + e);
        } finally {
            DBConnector.closeResultSet(resultSet);
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }

    @Override
    public HashMap<Long, Double> avgGenreWantSee(Long userId) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnector.getConnection();
            String query = "SELECT DISTINCT UNNEST(genres_ids) AS genres_ids, 10 AS avg_rate FROM want_see WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();
            HashMap<Long, Double> avgGenreRateMap = Maps.newHashMap();
            if (resultSet == null) {
                throw new DatabaseException("Error returning result.");
            }
            while (resultSet.next()) {
                Long genreId = resultSet.getLong("genres_ids");
                double avgRating = resultSet.getDouble("avg_rate");

                avgGenreRateMap.put(genreId, avgRating);
            }
            return avgGenreRateMap;
        } catch (SQLException e) {
            throw new DatabaseException("Problem with ID: " + e);
        } finally {
            DBConnector.closeResultSet(resultSet);
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }

    @Override
    public HashMap<Long, Double> avgProductionCompanyWantSee(Long userId) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnector.getConnection();
            String query = "SELECT DISTINCT UNNEST(production_companies) AS production_companies, 10 AS avg_rate FROM want_see WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();
            HashMap<Long, Double> avgProductionCompaniesRateMap = Maps.newHashMap();
            if (resultSet == null) {
                throw new DatabaseException("Error returning result.");
            }
            while (resultSet.next()) {
                Long companyId = resultSet.getLong("production_companies");
                double avgRating = resultSet.getDouble("avg_rate");

                avgProductionCompaniesRateMap.put(companyId, avgRating);
            }
            return avgProductionCompaniesRateMap;
        } catch (SQLException e) {
            throw new DatabaseException("Problem with ID: " + e);
        } finally {
            DBConnector.closeResultSet(resultSet);
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }

    @Override
    public HashMap<Long, Double> avgMainActorWantSee(Long userId) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnector.getConnection();
            String query = "SELECT DISTINCT main_actor_id, 10 AS avg_rate FROM want_see WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();
            HashMap<Long, Double> avgActorRateMap = Maps.newHashMap();
            if (resultSet == null) {
                throw new DatabaseException("Error returning result.");
            }
            while (resultSet.next()) {
                Long actorId = resultSet.getLong("main_actor_id");
                double avgRating = resultSet.getDouble("avg_rate");

                avgActorRateMap.put(actorId, avgRating);
            }
            return avgActorRateMap;
        } catch (SQLException e) {
            throw new DatabaseException("Problem with ID: " + e);
        } finally {
            DBConnector.closeResultSet(resultSet);
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }
}
