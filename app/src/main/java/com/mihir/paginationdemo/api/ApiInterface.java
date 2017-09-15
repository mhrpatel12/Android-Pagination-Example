package com.mihir.paginationdemo.api;

import com.mihir.paginationdemo.models.UsersResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Mihir on 14-09-2017.
 */

public interface ApiInterface {
    @GET("api/users?")
    Call<UsersResponse> getUsers(
            @Query("page") int page
    );
}
