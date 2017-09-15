package com.mihir.paginationdemo.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by patelmih on 8/24/2017.
 */

public class UserList implements Serializable {
    @SerializedName("id")
    private int userID;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("avatar")
    private String avatarImageURL;

    public UserList() {
    }

    public int getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAvatarImageURL() {
        return avatarImageURL;
    }
}
