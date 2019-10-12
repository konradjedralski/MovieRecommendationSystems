package com.jedralski.MovieRecommendationSystems.controller;

import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.exception.InputException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import com.jedralski.MovieRecommendationSystems.model.User;
import com.jedralski.MovieRecommendationSystems.service.MovieRatingsService;
import com.jedralski.MovieRecommendationSystems.service.MoviesApiService;
import com.jedralski.MovieRecommendationSystems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Controller
@RequestMapping("/add-rating")
public class AddRatingController {

    @Autowired
    private MoviesApiService moviesApiService;
    @Autowired
    private UserService userService;
    @Autowired
    private MovieRatingsService movieRatingsService;

    @RequestMapping("")
    public String addRating() {
        return "add-rating";
    }

    @PostMapping
    public String addRatingPost(@RequestParam("username") String username, @RequestParam("movie-title") String movieTitle, @RequestParam("rating") String rating, Model model) throws DatabaseException, InputException {

        if (rating.equals("chooseRating")) {
            model.addAttribute("checkStatus", 4);
            return "add-rating";
        }
        User user = userService.getUserDataByUsername(username);
        if (user == null) {
            model.addAttribute("checkStatus", 2);
            return "add-rating";
        }
        int userAge;
        if (user.getBirthDate() != null) {
            userAge = Period.between(user.getBirthDate().toLocalDate(), LocalDate.now()).getYears();
        } else {
            userAge = 0;
        }
        Long movieId = moviesApiService.findMovieIdByTitle(movieTitle);
        if (movieId == null) {
            model.addAttribute("checkStatus", 3);
            return "add-rating";
        }
        MovieRatings movieRatings = moviesApiService.findMovieDetailsByMovieId(movieId);
        List<Integer> genresIds = movieRatings.getGenresIds();
        List<String> productionCountries = movieRatings.getProductionCountries();
        List<Integer> productionCompanies = movieRatings.getProductionCompanies();

        if (genresIds.isEmpty() || productionCountries.isEmpty() || productionCompanies.isEmpty()) {
            model.addAttribute("checkStatus", 5);
            return "add-rating";
        } else {
            int mainActor = moviesApiService.findMainActorByMovieId(movieId);
            if (mainActor == 0) {
                model.addAttribute("checkStatus", 6);
                return "add-rating";
            } else {
                movieRatings = MovieRatings.builder()
                                           .movieId(movieId)
                                           .userId(user.getUserId())
                                           .rate(Integer.parseInt(rating))
                                           .userAge(userAge)
                                           .genresIds(genresIds)
                                           .productionCountries(productionCountries)
                                           .mainActorId(mainActor)
                                           .productionCompanies(productionCompanies)
                                           .build();

                if (movieRatingsService.addMovieRating(movieRatings)) {
                    model.addAttribute("checkStatus", 1);
                    return "add-rating";
                } else {
                    model.addAttribute("checkStatus", 0);
                    return "add-rating";
                }
            }
        }
    }
}

