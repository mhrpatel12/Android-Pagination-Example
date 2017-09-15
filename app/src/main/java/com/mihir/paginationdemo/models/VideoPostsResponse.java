package com.mihir.paginationdemo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VideoPostsResponse {

    @SerializedName("page")
    @Expose
    private Integer page;

    @SerializedName("posts")
    @Expose
    private List<VideoPost> videoPosts = new ArrayList<VideoPost>();

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<VideoPost> getVideoPosts() {
        return videoPosts;
    }

    public void setVideoPosts(List<VideoPost> videoPosts) {
        this.videoPosts = videoPosts;
    }

}
