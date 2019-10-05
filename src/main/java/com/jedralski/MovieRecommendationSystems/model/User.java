package com.jedralski.MovieRecommendationSystems.model;

import java.sql.Date;

public class User {

    private long userId;
    private String username;
    private String sex;
    private Date birthDate;
    private String nationality;

    public static final class Builder {
        private long userId;
        private String username;
        private String sex;
        private Date birthDate;
        private String nationality;

        public Builder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder sex(String sex) {
            this.sex = sex;
            return this;
        }

        public Builder birthDate(Date birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder nationality(String nationality) {
            this.nationality = nationality;
            return this;
        }

        public User build() {
            User user = new User();
            user.userId = this.userId;
            user.username = this.username;
            user.sex = this.sex;
            user.birthDate = this.birthDate;
            user.nationality = this.nationality;
            return user;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getSex() {
        return sex;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getNationality() {
        return nationality;
    }
}
