package com.example.franciscojavier.tfgproject.webapiclient;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

import com.example.franciscojavier.tfgproject.datamodel.*;

import java.util.List;
import java.util.Map;

public interface ApiTFGService {
    //Login (http://192.168.1.40:8080/api/usersmanager)
    @POST("/usersmanager")
    public void login(@Body MainUser credentials, Callback<MainUser> callback);

    //CreateUser (http://192.168.1.40:8080/api/usersmanager/PostCreateUser)
    @POST("/usersmanager/PostCreateUser")
    public void createUser(@Body MainUser user, Callback<MainUser> callback);

    //GetNearbyUsersByCompatibility (http://192.168.1.40:8080/api/calculateuserscompatibility/{id}
    @POST("/calculateuserscompatibility/{id}")
    public void getListOfNearbyUsers(@Path("id") Integer id, @Body MacsList macsList, Callback<List<String>> callback);
}
