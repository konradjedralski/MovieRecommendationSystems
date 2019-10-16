package com.jedralski.MovieRecommendationSystems.RecommendationSystemsDAO;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jedralski.MovieRecommendationSystems.connection.DBConnector;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import com.jedralski.MovieRecommendationSystems.model.Neighbour;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CollaborativeFilteringDAOImpl implements CollaborativeFilteringDAO {

    //List of every ratings from user. Has fields: book_id and rating.
    @Override
    public List<MovieRatings> userRatingsList(Long userId) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnector.getConnection();
            String query = "SELECT movie_id, rate FROM movie_ratings WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();
            List<MovieRatings> movieRatingsList = Lists.newArrayList();

            if (resultSet == null) {
                throw new DatabaseException("Error returning result.");
            }
            while (resultSet.next()) {
                Long movieId = resultSet.getLong("movie_id");
                int rate = resultSet.getInt("rate");

                movieRatingsList.add(MovieRatings.builder()
                                                 .movieId(movieId)
                                                 .rate(rate)
                                                 .build());
            }
            return movieRatingsList;
        } catch (SQLException e) {
            throw new DatabaseException("Problem with ID: " + e);
        } finally {
            DBConnector.closeResultSet(resultSet);
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }

    //List of users (neighbour) who have the same movies (as given user) which have been rating. Has fields : user_id, book_id and distance which now is 0.0.
    @Override
    public List<Neighbour> getNeighbour(List<MovieRatings> userRatingsList, Long userId) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        StringBuilder builder = new StringBuilder();
        try {
            connection = DBConnector.getConnection();

            for (int i = 0; i < userRatingsList.size(); i++) {
                builder.append("?,");
            }

            String query = "SELECT user_id, COUNT(*) AS movies_count FROM movie_ratings WHERE movie_id IN (" + builder.deleteCharAt(builder.length() - 1).toString() + ")AND user_id != ? GROUP BY user_id ORDER BY movies_count DESC LIMIT 50";
            preparedStatement = connection.prepareStatement(query);

            for (int i = 0; i < userRatingsList.size(); i++) {
                preparedStatement.setLong(i + 1, (userRatingsList.get(i).getMovieId()));
            }

            preparedStatement.setLong(1 + userRatingsList.size(), userId);
            resultSet = preparedStatement.executeQuery();
            List<Neighbour> neighbour = Lists.newArrayList();

            if (resultSet == null) {
                throw new DatabaseException("Error returning result.");
            }
            while (resultSet.next()) {
                Long neighbourId = resultSet.getLong("user_id");
                Long sameMovies = resultSet.getLong("movies_count");

                neighbour.add(Neighbour.builder()
                                       .userID(neighbourId)
                                       .sameMovies(sameMovies)
                                       .build());
            }
            return neighbour;
        } catch (SQLException e) {
            throw new DatabaseException("Problem with ID: " + e);
        } finally {
            DBConnector.closeResultSet(resultSet);
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }

    //Add distance to neighbour
    @Override
    public List<Neighbour> getNeighbourDistance(List<MovieRatings> userRatingsList, List<Neighbour> neighborhood) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Map<Long, Integer> moviesIndexes = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        List<Neighbour> neighborhood1 = Lists.newArrayList();
        List<Neighbour> neighborhood2 = Lists.newArrayList();
        try {
            connection = DBConnector.getConnection();

            for (int i = 0; i < userRatingsList.size(); i++) {
                builder.append("?,");
            }

            String query = "SELECT rate, movie_id FROM movie_ratings WHERE user_id = ? AND movie_id IN (" + builder.deleteCharAt(builder.length() - 1).toString() + ")";
            preparedStatement = connection.prepareStatement(query);

            for (int i = 0; i < userRatingsList.size(); i++) {
                preparedStatement.setLong(i + 2, (userRatingsList.get(i).getMovieId()));
                moviesIndexes.put(userRatingsList.get(i).getMovieId(), i);
            }

            for (Neighbour neighbour : neighborhood) {
                preparedStatement.setLong(1, neighbour.getUserId());
                resultSet = preparedStatement.executeQuery();
                if (resultSet == null) {
                    throw new DatabaseException("Error returning result.");
                }
                while (resultSet.next()) {
                    int rating = resultSet.getInt("rate");
                    int movieRate = userRatingsList.get(moviesIndexes.get(resultSet.getLong("movie_id"))).getRate();

                    neighbour = Neighbour.builder()
                                         .userID(neighbour.getUserId())
                                         .sameMovies(neighbour.getSameMovies())
                                         .distance(neighbour.getDistance() + Math.abs(rating - movieRate)) //Add the distance between user and neighbor based on the userRatingsList
                                         .build();
                }
                neighborhood1.add(neighbour);
            }

            for (Neighbour neighbour : neighborhood1) {
                neighbour = Neighbour.builder()
                                     .userID(neighbour.getUserId())
                                     .sameMovies(neighbour.getSameMovies())
                                     .distance((int) (neighbour.getDistance() / neighbour.getSameMovies())) //Divide result by the number of books so that the result is fair.
                                     .build();
                neighborhood2.add(neighbour);
            }
            Collections.sort(neighborhood2);
            return neighborhood2;
        } catch (SQLException e) {
            throw new DatabaseException("Problem with ID: " + e);
        } finally {
            DBConnector.closeResultSet(resultSet);
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }

    //List of user most liked genres
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

    //List of user most liked production countries
    @Override
    public HashMap<String, Double> avgProductionCountriesRating(Long userId) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnector.getConnection();
            String query = "SELECT UNNEST(production_countries_iso) AS production_countries_iso, AVG(rate) AS avg_rate FROM movie_ratings WHERE user_id = ? GROUP BY UNNEST(production_countries_iso)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();
            HashMap<String, Double> avgProductionCountriesRatingMap = Maps.newHashMap();
            if (resultSet == null) {
                throw new DatabaseException("Error returning result.");
            }
            while (resultSet.next()) {
                String countryIso = resultSet.getString("production_countries_iso");
                double avgRating = resultSet.getDouble("avg_rate");

                avgProductionCountriesRatingMap.put(countryIso, avgRating);
            }
            return avgProductionCountriesRatingMap;
        } catch (SQLException e) {
            throw new DatabaseException("Problem with ID: " + e);
        } finally {
            DBConnector.closeResultSet(resultSet);
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }

    //List of user most liked main actors
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

    //List of user want see movies
    @Override
    public List<Long> getWantSeeMovies(Long userId) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnector.getConnection();
            String query = "SELECT movie_id FROM want_see WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();
            List<Long> wantSeeList = Lists.newArrayList();
            if (resultSet == null) {
                throw new DatabaseException("Error returning result.");
            }
            while (resultSet.next()) {
                Long movieId = resultSet.getLong("movie_id");

                wantSeeList.add(movieId);
            }
            return wantSeeList;
        } catch (SQLException e) {
            throw new DatabaseException("Problem with ID: " + e);
        } finally {
            DBConnector.closeResultSet(resultSet);
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }

    @Override
    public List<MovieRatings> moviesToRecommended(List<MovieRatings> userRatingsList, List<Neighbour> neighborhood, List<Long> wantSeeMovies, HashMap<Long, Double> avgGenreRating, HashMap<String, Double> avgProductionCountriesRating, HashMap<Long, Double> avgMainActorRating) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnector.getConnection();
            List<MovieRatings> moviesToRecommend = Lists.newArrayList();
            int k;
            if (userRatingsList.size() < 5) {
                k = userRatingsList.size();
            } else {
                k = 5;
            }
            StringBuilder builderRating = new StringBuilder();
            StringBuilder builderUser = new StringBuilder();
            StringBuilder builderWantSee = new StringBuilder();
            for (int i = 0; i < userRatingsList.size(); i++) {
                builderRating.append("?,");
            }
            for (int i = 0; i < k; i++) {
                builderUser.append("?,");
            }
            for (int i = 0; i < wantSeeMovies.size(); i++) {
                builderWantSee.append("?,");
            }
            String query = "SELECT movie_id, rate, genres_ids, production_countries_iso, main_actor_id FROM movie_ratings WHERE user_id IN (" + builderUser.deleteCharAt(builderUser.length() - 1).toString() + ") AND movie_id NOT IN (" + builderRating.deleteCharAt(builderRating.length() - 1).toString() + ") AND movie_id NOT IN (" + builderWantSee.deleteCharAt(builderWantSee.length() - 1).toString() + ") ORDER BY rate DESC LIMIT 40";
            preparedStatement = connection.prepareStatement(query);
            for (int i = 0; i < k; i++) {
                preparedStatement.setLong(i + 1, (neighborhood.get(i).getUserId()));
            }
            int counter = k + 1;
            for (int i = 0; i < userRatingsList.size(); i++) {
                preparedStatement.setLong(counter, (userRatingsList.get(i).getMovieId()));
                counter++;
            }
            for (int i = 0; i < wantSeeMovies.size(); i++) {
                preparedStatement.setLong(counter, (wantSeeMovies.get(i)));
                counter++;
            }
            resultSet = preparedStatement.executeQuery();
            if (resultSet == null) {
                throw new DatabaseException("Error returning result.");
            }
            while (resultSet.next()) {
                Long movieId = resultSet.getLong("movie_id");
                int rate = resultSet.getInt("rate");
                Array genresIdsArray = resultSet.getArray("genres_ids");
                Array countriesIsoArray = resultSet.getArray("production_countries_iso");
                int mainActorId = resultSet.getInt("main_actor_id");
                Integer[] genresIdsInteger = (Integer[]) genresIdsArray.getArray();
                String[] countriesIsoString = (String[]) countriesIsoArray.getArray();

                MovieRatings recommendedMovies = MovieRatings.builder()
                                                             .movieId(movieId)
                                                             .rateRecommended(rate)
                                                             .build();

                double newRating = (0.5 * recommendedMovies.getRateRecommended());
                for (int genresIds : genresIdsInteger) {
                    if (!avgGenreRating.containsKey((long) genresIds)) {
                        newRating += ((0.25 / genresIdsInteger.length) * recommendedMovies.getRateRecommended());
                    } else {
                        newRating += ((0.25 / genresIdsInteger.length) * avgGenreRating.get((long) genresIds));
                    }
                }
                if (!avgMainActorRating.containsKey((long) mainActorId)) {
                    newRating += (0.15 * recommendedMovies.getRateRecommended());
                } else {
                    newRating += (0.15 * avgMainActorRating.get((long) mainActorId));
                }
                for (String countriesIso : countriesIsoString) {
                    if (!avgProductionCountriesRating.containsKey(countriesIso)) {
                        newRating += ((0.1 / countriesIsoString.length) * recommendedMovies.getRateRecommended());
                    } else {
                        newRating += ((0.1 / countriesIsoString.length) * avgProductionCountriesRating.get(countriesIso));
                    }
                }
                recommendedMovies = MovieRatings.builder()
                                                .movieId(movieId)
                                                .rate(rate)
                                                .rateRecommended(newRating)
                                                .build();
                moviesToRecommend.add(recommendedMovies);
            }
            Collections.sort(moviesToRecommend);
            return moviesToRecommend;
        } catch (SQLException e) {
            throw new DatabaseException("Problem with ID: " + e);
        } finally {
            DBConnector.closeResultSet(resultSet);
            DBConnector.closeStatement(preparedStatement);
            DBConnector.closeConnection(connection);
        }
    }
}
