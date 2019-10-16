package com.jedralski.MovieRecommendationSystems.recommendationSystems;

import com.google.common.collect.Lists;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Component
public class HybridMethod {

    public List<Long> getHybridRecommendation(List<MovieRatings> collaborativeList, List<MovieRatings> contentBasedList) {
        List<Long> moviesRecommended = Lists.newArrayList();
        for (MovieRatings collaborative : collaborativeList) {
            for (MovieRatings contentBased : contentBasedList) {
                if (collaborative.getMovieId() == contentBased.getMovieId()) {
                    moviesRecommended.add(contentBased.getMovieId());
                }
            }
        }
        for (Long movieId : moviesRecommended) {
            Iterator<MovieRatings> iteratorCollaborative = collaborativeList.iterator();
            while (iteratorCollaborative.hasNext()) {
                Long id = iteratorCollaborative.next().getMovieId();
                if (id.equals(movieId)) {
                    iteratorCollaborative.remove();
                }
            }
            Iterator<MovieRatings> iteratorContentBased = contentBasedList.iterator();
            while (iteratorContentBased.hasNext()) {
                Long id = iteratorContentBased.next().getMovieId();
                if (id.equals(movieId)) {
                    iteratorContentBased.remove();
                }
            }
        }
        Collections.sort(collaborativeList);
        Collections.sort(contentBasedList);
        if (moviesRecommended.size() < 20) {
            int counter = ((20 - moviesRecommended.size()) / 2);
            for (int i = 0; i < counter; i++) {
                moviesRecommended.add(collaborativeList.get(i).getMovieId());
            }
            for (int i = 0; i < counter; i++) {
                moviesRecommended.add(contentBasedList.get(i).getMovieId());
            }
            if (moviesRecommended.size() < 20) {
                moviesRecommended.add(collaborativeList.get(counter).getMovieId());
            }
        }
        if (moviesRecommended.size() > 20) {
            for (int i = (moviesRecommended.size() - 1); i > 20; i--) {
                moviesRecommended.remove(i);
            }
        }
        return moviesRecommended;
    }
}
