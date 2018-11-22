package com.example.bozsi.animalabuse;

public class Report {

    public String username;
    //public Bitmap image;
    public String image;
    public String longitude;
    public String latitude;
    public String message;

    public String getUsername() {
        return username;
    }

    //Not yet working
    /*public Bitmap getImage() {
        return image;
    }*/

    public String getImage(){
        return image;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getMessage() {
        return message;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //Not yet working
    /*public void setImage(Bitmap image) {
        this.image = image;
    }*/

    public void setImage(String image){
        this.image = image;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
