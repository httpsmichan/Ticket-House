package com.example.tickethouse;

public class reservationdata {
    private String booking_date;
    private String contact_no;
    private String email;
    private String movie_title;
    private String movie_time;
    private String name;
    private String payment_method;
    private String reservation_date;
    private String seat;
    private String theatre;
    private String code;

    // Default constructor
    public reservationdata() {
    }

    public reservationdata(String booking_date, String contact_no, String email, String movie_title, String movie_time,
                           String name, String payment_method, String reservation_date, String seat, String theatre, String code) {
        this.booking_date = booking_date;
        this.contact_no = contact_no;
        this.email = email;
        this.movie_title = movie_title;
        this.movie_time = movie_time;
        this.name = name;
        this.payment_method = payment_method;
        this.reservation_date = reservation_date;
        this.seat = seat;
        this.theatre = theatre;
        this.code = code;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public String getMovie_time() {
        return movie_time;
    }

    public void setMovie_time(String movie_time) {
        this.movie_time = movie_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getReservation_date() {
        return reservation_date;
    }

    public void setReservation_date(String reservation_date) {
        this.reservation_date = reservation_date;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getTheatre() {
        return theatre;
    }

    public void setTheatre(String code) {
        this.theatre = theatre;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
