package com.mihir.paginationdemo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mihir on 14-09-2017.
 */

public class VideoPost {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("thumbnail_image")
    @Expose
    private String thumbnailImage;

    @SerializedName("event_name")
    @Expose
    private String eventName;

    @SerializedName("event_date")
    @Expose
    private int eventDate;

    @SerializedName("views")
    @Expose
    private Integer views;

    @SerializedName("likes")
    @Expose
    private Integer likes;

    @SerializedName("shares")
    @Expose
    private Integer shares;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getEventDate() {
        return eventDate;
    }

    public void setEventDate(int eventDate) {
        this.eventDate = eventDate;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getShares() {
        return shares;
    }

    public void setShares(Integer shares) {
        this.shares = shares;
    }
}



