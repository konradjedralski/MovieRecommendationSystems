package com.jedralski.MovieRecommendationSystems.dao;

import com.jedralski.MovieRecommendationSystems.connection.DBConnector;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.exception.InputException;
import com.jedralski.MovieRecommendationSystems.model.User;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserDAOImpl implements UserDAO {

    @Override
    public boolean addUser(User user) throws InputException, DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBConnector.getConnection();
            String query = "INSERT INTO public.user (username, sex, birth_date, nationality, real) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getSex());
            preparedStatement.setDate(3, user.getBirthDate());
            preparedStatement.setString(4, user.getNationality());
            preparedStatement.setBoolean(5, true);
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
    public User getUserDataByUsername(String username) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnector.getConnection();
            String query = "SELECT * FROM public.user WHERE username LIKE ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            User user = null;
            if (resultSet == null) {
                throw new DatabaseException("Error returning result.");
            }
            if (resultSet.next()) {
                user = User.builder()
                           .userId(resultSet.getLong("id"))
                           .username(resultSet.getString("username"))
                           .sex(resultSet.getString("sex"))
                           .birthDate(resultSet.getDate("birth_date"))
                           .nationality(resultSet.getString("nationality"))
                           .build();
            }
            if (resultSet.next()) {
                throw new DatabaseException("Query returned more than 1 record.");
            }
            return user;
        } catch (SQLException e) {
            throw new DatabaseException("Exception: " + e);
        } finally {
            DBConnector.closeResultSet(resultSet);
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }


}
