package com.jedralski.MovieRecommendationSystems.model;

public class Neighbour implements Comparable<Neighbour>{

    private Long userId;
    private Long sameMovies;
    private int distance;

    public static final class Builder {
        private Long userId;
        private Long sameMovies;
        private int distance;

        public Builder userID(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder sameMovies(Long sameMovies) {
            this.sameMovies = sameMovies;
            return this;
        }

        public Builder distance(int distance) {
            this.distance = distance;
            return this;
        }

        public Neighbour build() {
            Neighbour neighbour = new Neighbour();
            neighbour.userId = this.userId;
            neighbour.sameMovies = this.sameMovies;
            neighbour.distance = this.distance;

            return neighbour;
        }
    }
    @Override
    public int compareTo(Neighbour o) {
        return (this.distance - o.distance);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSameMovies() {
        return sameMovies;
    }

    public int getDistance() {
        return distance;
    }
}
