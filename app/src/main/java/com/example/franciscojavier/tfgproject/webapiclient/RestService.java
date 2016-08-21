package com.example.franciscojavier.tfgproject.webapiclient;

import retrofit.RestAdapter;

public class RestService {
    private static final String URL ="http://192.168.1.40:8080/api/";
    private retrofit.RestAdapter restAdapter;
    private ApiTFGService apiTFGService;

    public RestService(){
        restAdapter = new retrofit.RestAdapter.Builder()
                .setEndpoint(URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        apiTFGService = restAdapter.create(ApiTFGService.class);
    }

    public ApiTFGService getApiTFGService(){
        return apiTFGService;
    }
}
