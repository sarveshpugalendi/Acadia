package com.example.acadia;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ThingSpeakApi {
    @GET("channels/{channel_id}/feeds.json")
    Call<ThingSpeakResponse> getChannelData(
            @Path("channel_id") int channelId,
            @Query("api_key") String apiKey,
            @Query("results") int results
    );
}

