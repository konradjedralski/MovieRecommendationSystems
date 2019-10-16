package com.jedralski.MovieRecommendationSystems.recommendationSystems;

import com.google.common.collect.Lists;
import com.jedralski.MovieRecommendationSystems.RecommendationSystemsService.ContentBasedFilteringService;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import com.jedralski.MovieRecommendationSystems.service.MoviesApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;

@Component
public class ContentBasedFiltering {

    @Autowired
    private ContentBasedFilteringService contentBasedFilteringService;
    @Autowired
    private MoviesApiService moviesApiService;

    private static final int limit = 15;

    public LinkedHashMap<Long, Double> sortGenres(Long userId) throws DatabaseException {
        HashMap<Long, Double> genreRating = contentBasedFilteringService.avgGenreRating(userId);
        HashMap<Long, Double> genreWantSee = contentBasedFilteringService.avgGenreWantSee(userId);

        for (Map.Entry<Long, Double> wantSee : genreWantSee.entrySet()) {
            for (Map.Entry<Long, Double> rating : genreRating.entrySet()) {
                if (wantSee.getKey() == rating.getKey()) {
                    double newRating = ((wantSee.getValue() * 0.15) + (rating.getValue() * 0.85));
                    rating.setValue(newRating);
                }
            }
        }

        LinkedHashMap<Long, Double> genreRatingSorted = genreRating.entrySet().stream().sorted(reverseOrder(Map.Entry.comparingByValue())).limit(limit)
                                                                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return genreRatingSorted;
    }

    public LinkedHashMap<Long, Double> sortProductionCompanies(Long userId) throws DatabaseException {
        HashMap<Long, Double> productionCompanyRating = contentBasedFilteringService.avgProductionCompanyRating(userId);
        HashMap<Long, Double> productionCompanyWantSee = contentBasedFilteringService.avgProductionCompanyWantSee(userId);

        for (Map.Entry<Long, Double> wantSee : productionCompanyWantSee.entrySet()) {
            for (Map.Entry<Long, Double> rating : productionCompanyRating.entrySet()) {
                if (wantSee.getKey() == rating.getKey()) {
                    double newRating = ((wantSee.getValue() * 0.15) + (rating.getValue() * 0.85));
                    rating.setValue(newRating);
                }
            }
        }

        LinkedHashMap<Long, Double> productionCompanyRatingSorted = productionCompanyRating.entrySet().stream().sorted(reverseOrder(Map.Entry.comparingByValue())).limit(limit)
                                                                                           .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return productionCompanyRatingSorted;
    }

    public LinkedHashMap<Long, Double> sortActors(Long userId) throws DatabaseException {
        HashMap<Long, Double> mainActorRating = contentBasedFilteringService.avgMainActorRating(userId);
        HashMap<Long, Double> mainActorWantSee = contentBasedFilteringService.avgMainActorWantSee(userId);

        for (Map.Entry<Long, Double> wantSee : mainActorWantSee.entrySet()) {
            for (Map.Entry<Long, Double> rating : mainActorRating.entrySet()) {
                if (wantSee.getKey() == rating.getKey()) {
                    double newRating = ((wantSee.getValue() * 0.15) + (rating.getValue() * 0.85));
                    rating.setValue(newRating);
                }
            }
        }

        LinkedHashMap<Long, Double> mainActorRatingSorted = mainActorRating.entrySet().stream().sorted(reverseOrder(Map.Entry.comparingByValue())).limit(limit)
                                                                           .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return mainActorRatingSorted;
    }

    public List<MovieRatings> addRecommendationRating(LinkedHashMap<Long, String> moviesToRecommendedContentBased, LinkedHashMap<Long, Double> genreRatingSorted, LinkedHashMap<Long, Double> productionCompanyRatingSorted, LinkedHashMap<Long, Double> mainActorRatingSorted) {
        List<MovieRatings> movieRatingsList = Lists.newArrayList();

        for (Map.Entry<Long, String> recommendedMovies : moviesToRecommendedContentBased.entrySet()) {
            MovieRatings movieRatings = moviesApiService.findMovieDetailsByMovieId(recommendedMovies.getKey());
            List<Integer> genresIds = movieRatings.getGenresIds();
            List<Integer> productionCompanies = movieRatings.getProductionCompanies();
            int mainActor = moviesApiService.findMainActorByMovieId(recommendedMovies.getKey());
            double newRating = 0;
            int i = limit;

            for (Map.Entry<Long, Double> genreRating : genreRatingSorted.entrySet()) {
                for (int genre : genresIds) {
                    if (genreRating.getKey() == (long) genre) {
                        newRating += (((0.7 / limit) * i) * genreRating.getValue());
                    }
                }
                i--;
            }
            i = limit;
            for (Map.Entry<Long, Double> companyRating : productionCompanyRatingSorted.entrySet()) {
                for (int company : productionCompanies) {
                    if (companyRating.getKey() == (long) company) {
                        newRating += (((0.1 / limit) * i) * companyRating.getValue());
                    }
                }
                i--;
            }
            i = limit;
            for (Map.Entry<Long, Double> actorRating : mainActorRatingSorted.entrySet()) {
                if (actorRating.getKey() == (long) mainActor) {
                    newRating += (((0.2 / limit) * i) * actorRating.getValue());
                }
                i--;
            }
            movieRatingsList.add(MovieRatings.builder()
                                             .movieId(recommendedMovies.getKey())
                                             .rateRecommended(newRating)
                                             .build());
        }
        Collections.sort(movieRatingsList);
        return movieRatingsList;
    }
}
