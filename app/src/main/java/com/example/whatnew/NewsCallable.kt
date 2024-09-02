package com.example.whatnew

import retrofit2.Call
import retrofit2.http.GET

//this interface contains all the end points you need to use =>get, post, put
interface NewsCallable {
    @GET("/v2/everything?q=apple&from=2024-08-26&to=2024-08-26&sortBy=popularity&apiKey=2fc4998785dd4cdf9f0298c3c617c4ae")
    fun getNews():Call<News>
}