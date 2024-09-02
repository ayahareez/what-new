package com.example.whatnew

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.app.ShareCompat.IntentBuilder
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.whatnew.databinding.ArticleListItemBinding

class NewsAdapter(val a: Activity, val articles: ArrayList<Article>) : Adapter<NewsAdapter.NewsVH>() {
    class NewsVH(val binding: ArticleListItemBinding) :ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsVH {
        val b=ArticleListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NewsVH(b)
    }

    override fun getItemCount()=  articles.size


    override fun onBindViewHolder(holder: NewsVH, position: Int) {
        Log.d("trace","link:${articles[position].urlToImage}")
        val url=articles[position].url
        holder.binding.articleTv.text=articles[position].title
        Glide
            .with(holder.binding.articleImage.context)
            .load(articles[position].urlToImage)
            .error(R.drawable.broken_image)
            .transition(DrawableTransitionOptions.withCrossFade(600))
            .into(holder.binding.articleImage)

        holder.binding.articleContainer.setOnClickListener {

            val i=Intent(Intent.ACTION_VIEW,url.toUri())
            a.startActivity(i)
        }
        holder.binding.shareFab.setOnClickListener {
            ShareCompat
                .IntentBuilder(a)
                .setType("text/plain")
                .setChooserTitle("share article with")
                .setText(url)
                .startChooser()
        }
    }


}