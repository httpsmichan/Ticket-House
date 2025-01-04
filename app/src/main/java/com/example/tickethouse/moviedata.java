package com.example.tickethouse;

import android.content.Intent;

public class moviedata {

    private String photoLink;
    private String title;
    private String price;
    private String director;
    private String description;
    private String cast;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String genre;
    private String theaters;
    private String languages;
    private String movieCode;

    // Default constructor required for Firebase
    public moviedata() {
    }

    // Parameterized constructor
    public moviedata(String photoLink, String title, String price, String director, String description,
                     String cast, String startDate, String endDate, String startTime, String endTime,
                     String genre, String theaters, String languages, String movieCode) {
        this.photoLink = photoLink;
        this.title = title;
        this.price = price;
        this.director = director;
        this.description = description;
        this.cast = cast;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.genre = genre;
        this.theaters = theaters;
        this.languages = languages;
        this.movieCode = movieCode;
    }

    // Getters and Setters
    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getTheaters() {
        return theaters;
    }

    public void setTheaters(String theaters) {
        this.theaters = theaters;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getMovieCode() {
        return movieCode;
    }

    public void setMovieCode(String movieCode) {
        this.movieCode = movieCode;
    }

    @Override
    public String toString() {
        return "MovieData{" +
                "photoLink='" + photoLink + '\'' +
                ", title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", director='" + director + '\'' +
                ", description='" + description + '\'' +
                ", cast='" + cast + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", genre='" + genre + '\'' +
                ", theaters='" + theaters + '\'' +
                ", languages='" + languages + '\'' +
                ", movieCode='" + movieCode + '\'' +
                '}';
    }
}
