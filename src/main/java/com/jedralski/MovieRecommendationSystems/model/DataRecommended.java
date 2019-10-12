package com.jedralski.MovieRecommendationSystems.model;

public class DataRecommended implements Comparable<DataRecommended> {

    private long genre;
    private long productionCompany;
    private long actor;
    private double rating;

    public static final class Builder {
        private long genre;
        private long productionCompany;
        private long actor;
        private double rating;

        public Builder genre(long genre) {
            this.genre = genre;
            return this;
        }

        public Builder productionCompany(long productionCompany) {
            this.productionCompany = productionCompany;
            return this;
        }

        public Builder actor(long actor) {
            this.actor = actor;
            return this;
        }

        public Builder rating(double rating) {
            this.rating = rating;
            return this;
        }

        public DataRecommended build() {
            DataRecommended dataRecommended = new DataRecommended();
            dataRecommended.genre = this.genre;
            dataRecommended.productionCompany = this.productionCompany;
            dataRecommended.actor = this.actor;
            dataRecommended.rating = this.rating;

            return dataRecommended;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int compareTo(DataRecommended o) {
        if (o.rating - this.rating < 0) return -1;
        else if (o.rating - this.rating > 0) return 1;
        else return 0;
    }

    public long getGenre() {
        return genre;
    }

    public long getProductionCompany() {
        return productionCompany;
    }

    public long getActor() {
        return actor;
    }

    public double getRating() {
        return rating;
    }
}
