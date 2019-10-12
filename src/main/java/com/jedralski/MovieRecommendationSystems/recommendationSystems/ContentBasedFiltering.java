package com.jedralski.MovieRecommendationSystems.recommendationSystems;

import com.jedralski.MovieRecommendationSystems.RecommendationSystemsService.ContentBasedFilteringService;
import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;

@Component
public class ContentBasedFiltering {

    @Autowired
    ContentBasedFilteringService contentBasedFilteringService;

//    public LinkedList<DataRecommended> dataRecommended(Long userId) throws DatabaseException {
//        LinkedHashMap<Long, Double> genreRating = sortGenres(userId);
//        LinkedHashMap<Long, Double> productionCompanyRating = sortProductionCompanies(userId);
//        LinkedHashMap<Long, Double> mainActorRating = sortActors(userId);
//        LinkedList<DataRecommended> dataRecommended = Lists.newLinkedList();
//
//        for (Map.Entry<Long, Double> genre : genreRating.entrySet()) {
//            for (Map.Entry<Long, Double> company : productionCompanyRating.entrySet()) {
//                for (Map.Entry<Long, Double> actor : mainActorRating.entrySet()) {
//                    dataRecommended.add(DataRecommended.builder()
//                                                       .genre(genre.getKey())
//                                                       .productionCompany(company.getKey())
//                                                       .actor(actor.getKey())
//                                                       .rating(((genre.getValue() * 0.7) + (actor.getValue() * 0.2) + (company.getValue() * 0.1)))
//                                                       .build());
//                }
//            }
//        }
//
//        Collections.sort(dataRecommended);
//        return dataRecommended; //.stream().limit(5).collect(Collectors.toCollection(LinkedList::new))
//    }

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

        LinkedHashMap<Long, Double> genreRatingSorted = genreRating.entrySet().stream().sorted(reverseOrder(Map.Entry.comparingByValue())).limit(15)
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

        LinkedHashMap<Long, Double> productionCompanyRatingSorted = productionCompanyRating.entrySet().stream().sorted(reverseOrder(Map.Entry.comparingByValue())).limit(15)
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

        LinkedHashMap<Long, Double> mainActorRatingSorted = mainActorRating.entrySet().stream().sorted(reverseOrder(Map.Entry.comparingByValue())).limit(15)
                                                                           .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return mainActorRatingSorted;
    }
}
