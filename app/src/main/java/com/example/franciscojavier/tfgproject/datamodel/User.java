package com.example.franciscojavier.tfgproject.datamodel;

public class User {
    private int id;
    private String name;
    private String bluetoothMac;
    private String cityAndCountry;
    private String job;
    private String hobbies;
    private String musicTastes;
    private String filmsTastes;
    private String readingTastes;

    public User(){
    }

    public User(String name, String cityAndCountry, String job, String hobbies, String musicTastes,
                String filmsTastes, String readingTastes){
        this.name = name;
        this.cityAndCountry = cityAndCountry;
        this.job = job;
        this.hobbies = hobbies;
        this.musicTastes = musicTastes;
        this.filmsTastes = filmsTastes;
        this.readingTastes = readingTastes;
    }

    public User(int id, String name, String cityAndCountry, String job, String hobbies,
                String musicTastes, String filmsTastes, String readingTastes){
        this.id = id;
        this.name = name;
        this.cityAndCountry = cityAndCountry;
        this.job = job;
        this.hobbies = hobbies;
        this.musicTastes = musicTastes;
        this.filmsTastes = filmsTastes;
        this.readingTastes = readingTastes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityAndCountry() {
        return cityAndCountry;
    }

    public void setCityAndCountry(String cityAndCountry) {
        this.cityAndCountry = cityAndCountry;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getMusicTastes() {
        return musicTastes;
    }

    public void setMusicTastes(String musicTastes) {
        this.musicTastes = musicTastes;
    }

    public String getFilmsTastes() {
        return filmsTastes;
    }

    public void setFilmsTastes(String filmsTastes) {
        this.filmsTastes = filmsTastes;
    }

    public String getReadingTastes() {
        return readingTastes;
    }

    public void setReadingTastes(String readingTastes) {
        this.readingTastes = readingTastes;
    }

    public String getBluetoothMac() {
        return bluetoothMac;
    }

    public void setBluetoothMac(String bluetoothMac) {
        this.bluetoothMac = bluetoothMac;
    }
}
