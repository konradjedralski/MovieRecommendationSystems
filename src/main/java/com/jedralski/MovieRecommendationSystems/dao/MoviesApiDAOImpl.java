package com.jedralski.MovieRecommendationSystems.dao;

import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import com.jedralski.MovieRecommendationSystems.connection.HttpConnection;
import com.jedralski.MovieRecommendationSystems.exception.ApiException;
import com.jedralski.MovieRecommendationSystems.factory.ConnectionFactory;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MoviesApiDAOImpl implements MoviesApiDAO {

    private static final String API_KEY = "e9299e8865b7480b1465489c05023c4a";
    private static final String API_URL_FIND_MOVIE_BY_TITLE = "https://api.themoviedb.org/3/search/movie?api_key=%s&query=%s";
    private static final String API_URL_FIND_MOVIE_DETAILS_BY_MOVIE_ID = "https://api.themoviedb.org/3/movie/%s?api_key=%s&language=en-US";
    private static final String API_URL_FIND_MOVIE_CREDITS_BY_MOVIE_ID = "https://api.themoviedb.org/3/movie/%s/credits?api_key=%s";

    private final ConnectionFactory connectionFactory;

    public MoviesApiDAOImpl() {
        this(new ConnectionFactory());
    }

    public MoviesApiDAOImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Long findMovieIdByTitle(String title) throws ApiException {
        String prepareReplaceTitle = title.replace(' ', '+');
        String requestURL = String.format(API_URL_FIND_MOVIE_BY_TITLE, API_KEY, prepareReplaceTitle);
        String response = getResponse(requestURL);
        try {
            int movieId = JsonPath.read(response, "$['results'][0]['id']");
            return (long) movieId;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public MovieRatings findMovieDetailsByMovieId(long movieId) throws ApiException {
        String requestURL = String.format(API_URL_FIND_MOVIE_DETAILS_BY_MOVIE_ID, movieId, API_KEY);
        String response = getResponse(requestURL);
        List<Integer> genresIds = Lists.newArrayList();
        List<String> productionCountries = Lists.newArrayList();
        try {
            List<Integer> genresIdsJson = JsonPath.parse(response).read("$['genres'][*]['id']");
            List<String> productionCountriesJson = JsonPath.parse(response).read("$['production_countries'][*]['iso_3166_1']");
            for (Integer genres : genresIdsJson) {
                genresIds.add(genres);
            }

            for (String countries : productionCountriesJson) {
                productionCountries.add(countries);
            }
            return MovieRatings.builder()
                               .genresIds(genresIds)
                               .productionCountries(productionCountries)
                               .build();
        } catch (Exception ex) {
            return MovieRatings.builder()
                               .genresIds(genresIds)
                               .productionCountries(productionCountries)
                               .build();
        }
    }

    @Override
    public int findMainActorByMovieId(long movieId) throws ApiException {
        String requestURL = String.format(API_URL_FIND_MOVIE_CREDITS_BY_MOVIE_ID, movieId, API_KEY);
        String response = getResponse(requestURL);
        try {
            return JsonPath.read(response, "$['cast'][0]['id']");
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public String findMovieTitleById(long movieId) throws ApiException {
        String requestURL = String.format(API_URL_FIND_MOVIE_DETAILS_BY_MOVIE_ID, movieId, API_KEY);
        String response = getResponse(requestURL);
        return JsonPath.read(response, "$['title']");
    }

    private String getResponse(String requestURL) {
        try (HttpConnection connection = connectionFactory.build(requestURL)) {
            String response = connection.response();
            return response;
        } catch (Exception ex) {
            return null;
        }
    }
}
