package com.example.newsfeedappdemo.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.example.newsfeedappdemo.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_news_details.*
import java.text.SimpleDateFormat

class NewsDetailActivity : AppCompatActivity() {

    /*private val placeHolderImage =
        "https://pbs.twimg.com/profile_images/467502291415617536/SP8_ylk9.png"
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)

        val author = intent.getStringExtra("author")
        val content = intent.getStringExtra("content")
        val description = intent.getStringExtra("description")
        val publishedAt = intent.getStringExtra("publishedAt")
        val title = intent.getStringExtra("title")
        val url = intent.getStringExtra("url")
        val urlToImage = intent.getStringExtra("urlToImage")
        val sourceName = intent.getStringExtra("sourceName")
        //YYYY-MM-DDTHH:MM:SSZ

        try {
            val parser = SimpleDateFormat(getString(R.string.api_date_format))
            val formatter = SimpleDateFormat(getString(R.string.date_display_format))
            val output = formatter.format(parser.parse(publishedAt))

            articleTitle.text = HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
            articleContent.text = HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY)
            articleDescription.text = HtmlCompat.fromHtml(
                "$description\n - $author",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            articleSource.text = getString(R.string.by) + sourceName

            articlePublishedAt.text = getString(R.string.updated_on) + output
            checkForUrlToImage(urlToImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        articleSourceLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun checkForUrlToImage(urlToImage: String) {
        if (urlToImage == null || urlToImage.isEmpty()) {
            Picasso.get()
                .load(R.drawable.ic_warning_black_24dp)
                .centerCrop()
                .placeholder(R.drawable.ic_autorenew_black_24dp)
                .fit()
                .into(articleUrlToImage)
        } else {
            Picasso.get()
                .load(urlToImage)
                .centerCrop()
                .placeholder(R.drawable.ic_autorenew_black_24dp)
                .fit()
                .into(articleUrlToImage)
        }
    }
}