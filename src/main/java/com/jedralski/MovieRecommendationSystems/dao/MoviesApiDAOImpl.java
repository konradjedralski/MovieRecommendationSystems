package com.jedralski.MovieRecommendationSystems.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.JsonPath;
import com.jedralski.MovieRecommendationSystems.connection.HttpConnection;
import com.jedralski.MovieRecommendationSystems.exception.ApiException;
import com.jedralski.MovieRecommendationSystems.factory.ConnectionFactory;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MoviesApiDAOImpl implements MoviesApiDAO {

    private static final String API_KEY = "e9299e8865b7480b1465489c05023c4a";
    private static final String API_URL_FIND_MOVIE_BY_TITLE = "https://api.themoviedb.org/3/search/movie?api_key=%s&query=%s";
    private static final String API_URL_FIND_MOVIE_DETAILS_BY_MOVIE_ID = "https://api.themoviedb.org/3/movie/%s?api_key=%s&language=en-US";
    private static final String API_URL_FIND_MOVIE_CREDITS_BY_MOVIE_ID = "https://api.themoviedb.org/3/movie/%s/credits?api_key=%s";
    private static final String API_URL_FIND_MOVIE_TITLE_BY_DETAILS = "https://api.themoviedb.org/3/discover/movie?api_key=%s&language=en-US&sort_by=popularity.desc&include_video=false&page=1&with_cast=%s&with_companies=%s&with_genres=%s";

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
        List<Integer> productionCompanies = Lists.newArrayList();
        try {
            List<Integer> genresIdsJson = JsonPath.parse(response).read("$['genres'][*]['id']");
            List<String> productionCountriesJson = JsonPath.parse(response).read("$['production_countries'][*]['iso_3166_1']");
            List<Integer> productionCompaniesJson = JsonPath.parse(response).read("$['production_companies'][*]['id']");
            for (Integer genres : genresIdsJson) {
                genresIds.add(genres);
            }
            for (String countries : productionCountriesJson) {
                productionCountries.add(countries);
            }
            for (Integer companies : productionCompaniesJson) {
                productionCompanies.add(companies);
            }
            return MovieRatings.builder()
                               .genresIds(genresIds)
                               .productionCountries(productionCountries)
                               .productionCompanies(productionCompanies)
                               .build();
        } catch (Exception ex) {
            return MovieRatings.builder()
                               .genresIds(genresIds)
                               .productionCountries(productionCountries)
                               .productionCompanies(productionCompanies)
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

    @Override
    public Map<Integer, String> findMovieTitlesByDetails(LinkedHashMap<Long, Double> genreRatingSorted, LinkedHashMap<Long, Double> productionCompanyRatingSorted, LinkedHashMap<Long, Double> mainActorRatingSorted) throws ApiException {
        StringBuilder builderGenres = new StringBuilder();
        StringBuilder builderCompanies = new StringBuilder();
        StringBuilder builderActors = new StringBuilder();
        Map<Integer, String> movies = Maps.newHashMap();

        for(Map.Entry<Long, Double> genre : genreRatingSorted.entrySet()){
            builderGenres.append(genre.getKey() + "|");
        }
        for(Map.Entry<Long, Double> company : productionCompanyRatingSorted.entrySet()){
            builderCompanies.append(company.getKey() + "|");
        }
        for(Map.Entry<Long, Double> actor : mainActorRatingSorted.entrySet()){
            builderActors.append(actor.getKey() + "|");
        }

        String requestURL = String.format(API_URL_FIND_MOVIE_TITLE_BY_DETAILS, API_KEY ,builderActors.deleteCharAt(builderActors.length() - 1).toString(), builderCompanies.deleteCharAt(builderCompanies.length() - 1).toString(), builderGenres.deleteCharAt(builderGenres.length() - 1).toString());
        String response = getResponse(requestURL);
        try {
            List<Integer> moviesIds = JsonPath.read(response, "$['results'][*]['id']");
            List<String> moviesTitles = JsonPath.read(response, "$['results'][*]['title']");

            for (int i = 0; i < moviesIds.size(); i++) {
                movies.put(moviesIds.get(i), moviesTitles.get(i));
            }

            return movies;
        } catch (Exception ex) {
            return null;
        }
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
