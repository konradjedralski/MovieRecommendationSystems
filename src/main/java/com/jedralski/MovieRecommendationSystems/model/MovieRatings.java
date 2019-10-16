package com.jedralski.MovieRecommendationSystems.model;

import java.util.List;

public class MovieRatings implements Comparable<MovieRatings> {

    private long movieId;
    private long userId;
    private int rate;
    private int userAge;
    private List<Integer> genresIds;
    private List<String> productionCountries;
    private int mainActorId;
    private double rateRecommended;
    private List<Integer> productionCompanies;

    public static final class Builder {
        private long movieId;
        private long userId;
        private int rate;
        private int userAge;
        private List<Integer> genresIds;
        private List<String> productionCountries;
        private int mainActorId;
        private double rateRecommended;
        private List<Integer> productionCompanies;

        public Builder movieId(long movieId) {
            this.movieId = movieId;
            return this;
        }

        public Builder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder rate(int rate) {
            this.rate = rate;
            return this;
        }

        public Builder userAge(int userAge) {
            this.userAge = userAge;
            return this;
        }

        public Builder genresIds(List<Integer> genresIds) {
            this.genresIds = genresIds;
            return this;
        }

        public Builder productionCountries(List<String> productionCountries) {
            this.productionCountries = productionCountries;
            return this;
        }

        public Builder mainActorId(int mainActorId) {
            this.mainActorId = mainActorId;
            return this;
        }

        public Builder rateRecommended(double rateRecommended) {
            this.rateRecommended = rateRecommended;
            return this;
        }

        public Builder productionCompanies(List<Integer> productionCompanies) {
            this.productionCompanies = productionCompanies;
            return this;
        }

        public MovieRatings build() {
            MovieRatings movieRatings = new MovieRatings();
            movieRatings.movieId = this.movieId;
            movieRatings.userId = this.userId;
            movieRatings.rate = this.rate;
            movieRatings.userAge = this.userAge;
            movieRatings.genresIds = this.genresIds;
            movieRatings.productionCountries = this.productionCountries;
            movieRatings.mainActorId = this.mainActorId;
            movieRatings.rateRecommended = this.rateRecommended;
            movieRatings.productionCompanies = this.productionCompanies;
            return movieRatings;
        }
    }

    @Override
    public int compareTo(MovieRatings o) {
        if (o.rateRecommended - this.rateRecommended < 0) return -1;
        else if (o.rateRecommended - this.rateRecommended > 0) return 1;
        else return 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getMovieId() {
        return movieId;
    }

    public long getUserId() {
        return userId;
    }

    public int getRate() {
        return rate;
    }

    public int getUserAge() {
        return userAge;
    }

    public List<Integer> getGenresIds() {
        return genresIds;
    }

    public List<String> getProductionCountries() {
        return productionCountries;
    }

    public int getMainActorId() {
        return mainActorId;
    }

    public double getRateRecommended() {
        return rateRecommended;
    }

    public List<Integer> getProductionCompanies() {
        return productionCompanies;
    }
}
