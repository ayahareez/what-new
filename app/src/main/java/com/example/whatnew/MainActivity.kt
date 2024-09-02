package com.example.whatnew

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.whatnew.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set up the toolbar as the app bar
        setSupportActionBar(binding.toolbar)

        // Check if the user is logged in
        if (auth.currentUser == null) {
            // If not logged in, navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Finish this activity so the user can't go back to it
            return
        }

        // Load news when the activity is created
        loadNews()

        // Set up the SwipeRefreshLayout's refresh listener
        binding.swipeRefresh.setOnRefreshListener {
            loadNews()
        }
    }

    // Inflate the menu; this adds items to the action bar if it is present
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handle action bar item clicks here
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Handle the logout action
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Finish this activity so the user can't go back to it
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Function to load news using Retrofit
    private fun loadNews() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val newsService = retrofit.create(NewsCallable::class.java)
        newsService.getNews().enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                if (response.isSuccessful) {
                    val news = response.body()
                    val articles = news?.articles ?: ArrayList()
                    articles.removeAll { it.title == "[Removed]" || it.urlToImage == null }
                    showNews(articles)
                    binding.progressBar.isVisible = false
                    binding.swipeRefresh.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                binding.progressBar.isVisible = false
                binding.swipeRefresh.isRefreshing = false
            }
        })
    }

    // Function to display news articles in the RecyclerView
    private fun showNews(articles: ArrayList<Article>) {
        val adapter = NewsAdapter(this, articles)
        binding.newsRv.adapter = adapter
    }
}