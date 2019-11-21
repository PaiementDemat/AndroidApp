package com.paiementdemat.mobilepay.data;

import android.util.Log;
import android.view.View;

import com.paiementdemat.mobilepay.api.ApiUtils;
import com.paiementdemat.mobilepay.api.AuthService;
import com.paiementdemat.mobilepay.api.User;
import com.paiementdemat.mobilepay.data.model.LoggedInUser;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.paiementdemat.mobilepay.QRead.TAG;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private AuthService mAPIService;
    private LoggedInUser user;

    public Result<LoggedInUser> login(String username, String password) {

        try {
            Log.d(TAG, "In login");
            this.mAPIService = ApiUtils.getAPIService();
            User user = new User();
            user.setEmail(username);
            user.setPassword(password);
            sendPost(user.toString());
            // TODO: handle loggedInUser authentication

            /*LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");*/
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    public void sendPost(String body) {
        mAPIService.login(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()) {
                    showResponse(response.body().toString());
                    Log.i(TAG, "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Unable to submit post to API.");
            }
        });
    }

    public void showResponse(String response) {
        user = new LoggedInUser(java.util.UUID.randomUUID().toString(), response);
    }

}
