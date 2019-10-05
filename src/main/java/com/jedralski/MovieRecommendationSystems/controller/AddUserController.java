package com.jedralski.MovieRecommendationSystems.controller;

import com.jedralski.MovieRecommendationSystems.exception.DatabaseException;
import com.jedralski.MovieRecommendationSystems.exception.InputException;
import com.jedralski.MovieRecommendationSystems.model.User;
import com.jedralski.MovieRecommendationSystems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Controller
@RequestMapping("/add-user")
public class AddUserController {

    @Autowired
    UserService userService;

    @RequestMapping("")
    public String addUser() {
        return "add-user";
    }

    @PostMapping
    public String addUserPost(@RequestParam("username") String username, @RequestParam("sex") String sex, @RequestParam("birth-date") String birthDate, @RequestParam("nationality") String nationality, Model model) throws DatabaseException, InputException, ParseException {

        if (sex.equals("chooseSex")) {
            model.addAttribute("checkStatus", 2);
            return "add-user";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = format.parse(birthDate);
        java.sql.Date sqlBirthDate = new java.sql.Date(date.getTime());

        User user = User.builder()
                        .username(username)
                        .sex(sex)
                        .birthDate(sqlBirthDate)
                        .nationality(nationality.toUpperCase())
                        .build();

        if (userService.addUser(user)) {
            model.addAttribute("checkStatus", 1);
        } else {
            model.addAttribute("checkStatus", 0);
        }

        return "add-user";
    }
}
