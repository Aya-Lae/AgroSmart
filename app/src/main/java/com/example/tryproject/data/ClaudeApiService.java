package com.example.tryproject.data;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import java.util.List;
import java.util.Map;

public interface ClaudeApiService {

    @Headers({
            "content-type: application/json",
            "anthropic-version: 2023-06-01"
    })
    @POST("v1/messages")
    Call<Map<String, Object>> envoyerMessage(@Body Map<String, Object> body);
}