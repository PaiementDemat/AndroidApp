package com.paiementdemat.mobilepay.api;

public class ApiUtils {

    private ApiUtils() {}

//    public static final String BASE_URL = "http://10.1.178.239:9999/";
    public static final String BASE_URL = "http://localhost:9999/";

    public static AuthService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(AuthService.class);
    }
}
