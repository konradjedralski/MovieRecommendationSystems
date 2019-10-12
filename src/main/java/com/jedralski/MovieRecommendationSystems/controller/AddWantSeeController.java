package com.jedralski.MovieRecommendationSystems.controller;

import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.exception.InputException;
import com.jedralski.MovieRecommendationSystems.model.MovieRatings;
import com.jedralski.MovieRecommendationSystems.model.User;
import com.jedralski.MovieRecommendationSystems.service.MoviesApiService;
import com.jedralski.MovieRecommendationSystems.service.UserService;
import com.jedralski.MovieRecommendationSystems.service.WantSeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/want-see")
public class AddWantSeeController {

    @Autowired
    private WantSeeService wantSeeService;
    @Autowired
    private MoviesApiService moviesApiService;
    @Autowired
    private UserService userService;

    @RequestMapping("")
    public String addWantSee() {
        return "add-want-see";
    }

    @PostMapping
    public String addWantSeePost(@RequestParam("username") String username, @RequestParam("movie-title") String movieTitle, Model model) throws DatabaseException, InputException {

        User user = userService.getUserDataByUsername(username);
        if (user == null) {
            model.addAttribute("checkStatus", 2);
            return "add-want-see";
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
            model.addAttribute("checkStatus", 4);
            return "add-want-see";
        } else {
            int mainActor = moviesApiService.findMainActorByMovieId(movieId);
            if (mainActor == 0) {
                model.addAttribute("checkStatus", 5);
                return "add-want-see";
            } else {
                movieRatings = MovieRatings.builder()
                                           .movieId(movieId)
                                           .userId(user.getUserId())
                                           .genresIds(genresIds)
                                           .productionCountries(productionCountries)
                                           .mainActorId(mainActor)
                                           .productionCompanies(productionCompanies)
                                           .build();

                if (wantSeeService.addWantSee(movieRatings)) {
                    model.addAttribute("checkStatus", 1);
                    return "add-want-see";
                } else {
                    model.addAttribute("checkStatus", 0);
                    return "add-want-see";
                }
            }
        }
    }
}
