package com.example.whatnew

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.whatnew.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

   //  https://newsapi.org/v2/top-headlines?country=us&category=general&apiKey=2fc4998785dd4cdf9f0298c3c617c4ae&pageSize=30

    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadNews()
        binding.swipeRefresh.setOnRefreshListener {
            loadNews()
        }

    }

    private fun loadNews(){
        val retrofit=Retrofit
            .Builder()//builder pattern enables the share of methods
            .baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val c =retrofit.create(NewsCallable::class.java)
        c.getNews().enqueue(object :Callback<News>{
            override fun onResponse(p0: Call<News>, p1: Response<News>) {
               if(p1.isSuccessful){
                   val news=p1.body()
                   val articles=news?.articles!!//safe call operator=> ?.
                   articles.removeAll{
                       it.title=="[Removed]"
                       it.urlToImage==null
                   }
                   //Log.d("trace","Articles: $articles")
                   showNews(articles)
                   binding.progressBar.isVisible=false
                   binding.swipeRefresh.isRefreshing=false
               }
            }

            override fun onFailure(p0: Call<News>, p1: Throwable) {
                Log.d("trace","Error: ${p1.message}")
                binding.progressBar.isVisible=false
            }
        })
    }
    private fun showNews( articles:ArrayList<Article>){
        val adapter=NewsAdapter(this, articles)
        binding.newsRv.adapter=adapter

    }
}