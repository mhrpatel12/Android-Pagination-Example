package com.mihir.paginationdemo.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by patelmih on 8/24/2017.
 */

public class UsersResponse {
    @SerializedName("data")
    public List<UserList> userList;

    public List<UserList> getUserList() {
        return userList;
    }

    public void setUserList(List<UserList> userList) {
        this.userList = userList;
    }
}
