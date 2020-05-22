package com.example.newsfeedappdemo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.newsfeedappdemo.R
import com.example.newsfeedappdemo.model.Article
import com.example.newsfeedappdemo.ui.NewsDetailActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_article.view.*

class ArticleAdapter(
    private var articleList: ArrayList<Article>
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private lateinit var viewGroupContext: Context

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ArticleViewHolder {
        viewGroupContext = viewGroup.context
        val itemView: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_article, viewGroup, false)
        return ArticleViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    override fun onBindViewHolder(articleViewHolder: ArticleViewHolder, itemIndex: Int) {
        val article: Article = articleList.get(itemIndex)
        setPropertiesForArticleViewHolder(articleViewHolder, article)
        articleViewHolder.cardView.setOnClickListener {
            val intent = Intent(viewGroupContext, NewsDetailActivity::class.java)
            intent.putExtra("author", article.author)
            intent.putExtra("content", article.content)
            intent.putExtra("description", article.description)
            intent.putExtra("publishedAt", article.publishedAt)
            intent.putExtra("title", article.title)
            intent.putExtra("url", article.url)
            intent.putExtra("urlToImage", article.urlToImage)
            intent.putExtra("sourceName", article.source.name)
            viewGroupContext.startActivity(intent)
        }
    }

    private fun setPropertiesForArticleViewHolder(
        articleViewHolder: ArticleViewHolder,
        article: Article
    ) {
        //HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY);
        checkForUrlToImage(article, articleViewHolder)

        if (article.title != null && !article.title.isEmpty())
        articleViewHolder.title.text = HtmlCompat.fromHtml(
            article.title,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        if (article.description != null && !article.description.isEmpty())
        articleViewHolder.description.text = HtmlCompat.fromHtml(
            article.description,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        if (article.source.name != null && !article.source.name.isEmpty())
        articleViewHolder.articleSource.text = HtmlCompat.fromHtml(
            article.source.name,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        if (article.author != null && !article.author.isEmpty())
            articleViewHolder.articleAuthor.text =
                HtmlCompat.fromHtml(
                    article.author,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                );//article.author
    }

    private fun checkForUrlToImage(article: Article, articleViewHolder: ArticleViewHolder) {
        if (article.urlToImage == null || article.urlToImage.isEmpty()) {
            Picasso.get()
                .load(R.drawable.ic_warning_black_24dp)
                .centerCrop()
                .placeholder(R.drawable.ic_autorenew_black_24dp)
                .fit()
                .into(articleViewHolder.urlToImage)
        } else {
            Picasso.get()
                .load(article.urlToImage)
                .centerCrop()
                .placeholder(R.drawable.ic_autorenew_black_24dp)
                .fit()
                .into(articleViewHolder.urlToImage)
        }
    }

    fun setArticles(articles: ArrayList<Article>) {
        articleList = articles
        notifyDataSetChanged()
    }

    inner class ArticleViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val cardView: LinearLayout by lazy { view.card_view }
        val urlToImage: ImageView by lazy { view.article_urlToImage }
        val title: TextView by lazy { view.article_title }
        val description: TextView by lazy { view.article_description }
        val articleSource: TextView by lazy { view.articleSource }
        val articleAuthor: TextView by lazy { view.articleAuthor }
    }
}