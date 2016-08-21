package com.example.franciscojavier.tfgproject.datamodel;

public class MainUser extends User{
    private String password;

    public MainUser(){
        super();
    }

    public MainUser(String name, String password, String cityAndCountry, String job, String hobbies,
                    String musicTastes, String filmsTastes, String readingTastes){
        super(name, cityAndCountry, job, hobbies, musicTastes, filmsTastes, readingTastes);
        this.password = password;
    }

    public MainUser(int id, String name, String password, String cityAndCountry, String job,
                    String hobbies, String musicTastes, String filmsTastes, String readingTastes){
        super(id, name, cityAndCountry, job, hobbies, musicTastes, filmsTastes, readingTastes);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
