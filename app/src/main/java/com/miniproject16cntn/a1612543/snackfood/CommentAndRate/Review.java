package com.miniproject16cntn.a1612543.snackfood.CommentAndRate;

public class Review {
    private String user;
    private int restaurant;
    private String timestamp;
    private int score;
    private String comment;

    public Review(String user, int restaurant, String timestamp, int score, String comment) {
        this.user = user;
        this.restaurant = restaurant;
        this.timestamp = timestamp;
        this.score = score;
        this.comment = comment;
    }

    public Review() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(int restaurant) {
        this.restaurant = restaurant;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
