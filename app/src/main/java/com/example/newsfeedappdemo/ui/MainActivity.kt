package com.example.newsfeedappdemo.ui

import android.app.SearchManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.newsfeedappdemo.R
import com.example.newsfeedappdemo.adapter.ArticleAdapter
import com.example.newsfeedappdemo.model.Article
import com.example.newsfeedappdemo.model.TopHeadlines
import com.example.newsfeedappdemo.news_api.AppDatabase
import com.example.newsfeedappdemo.news_api.APIEndpoint
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private val BASE_URL by lazy { "https://newsapi.org/v2/" }

    private lateinit var apiEndpoint: APIEndpoint
    private lateinit var newsApiConfig: String
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var articleList: ArrayList<Article>
    private lateinit var userKeyWordInput: String

    private lateinit var topHeadlinesObservable: Observable<TopHeadlines>
    private lateinit var compositeDisposable: CompositeDisposable
    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Network request
        val retrofit: Retrofit = generateRetrofitBuilder()
        apiEndpoint = retrofit.create(APIEndpoint::class.java)
        newsApiConfig = resources.getString(R.string.api_key)

        swipe_refresh.setOnRefreshListener(this)
        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
        articleList = ArrayList()
        articleAdapter = ArticleAdapter(articleList)


        userKeyWordInput = ""

        compositeDisposable = CompositeDisposable()
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = articleAdapter
    }

    fun fetchData() {
        if (isConnected(this)) {
            Log.d("callFrom:", "Network")
            checkUserKeywordInput()
            swipe_refresh.isRefreshing = true
            swipe_refresh.isEnabled = true
        } else {
            Log.d("callFrom:", "Local")
            //disable swipe
            //disable search
            swipe_refresh.isRefreshing = false
            swipe_refresh.isEnabled = false
            getDBData()
        }
    }

    /*
    * Step1- Check Local Db null === Internet and Api Call + Insert Data*/

    fun getDBData() {
        doAsync {
            articleList.addAll(AppDatabase.getInstance(this@MainActivity).mDao().getAll)
            Log.d("articleListDB", Gson().toJson(articleList))
            articleAdapter.notifyDataSetChanged()
        }
    }

    fun insertDataInDB(mData: Article) {
        doAsync {
            AppDatabase.getInstance(this@MainActivity).mDao().insertAll(mData)
        }
        getDBData()
    }

    fun isConnected(context: Context): Boolean {
        val cm =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun deleteAllDb() {
        doAsync {
            AppDatabase.getInstance(this@MainActivity).mDao().deleteAll()
        }
        getDBData()
    }

    override fun onStart() {
        super.onStart()
        //fetchData()

    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onRefresh() {
        if (isConnected(this))
            checkUserKeywordInput()
    }

    private fun checkUserKeywordInput() {
        if (userKeyWordInput.isEmpty()) {
            queryTopHeadlines()
        } else {
            getKeyWordQuery(userKeyWordInput)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.menu_main, menu)
            //Creates input field for the user search
            setUpSearchMenuItem(menu)
        }
        return true
    }

    private fun setUpSearchMenuItem(menu: Menu) {
        val searchManager: SearchManager =
            (getSystemService(Context.SEARCH_SERVICE)) as SearchManager
        val searchView: SearchView = ((menu.findItem(R.id.action_search)?.actionView)) as SearchView
        val searchMenuItem: MenuItem = menu.findItem(R.id.action_search)

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Search..."
        searchView.setOnQueryTextListener(onQueryTextListenerCallback())
        searchMenuItem.icon.setVisible(false, false)
    }

    //Gets immediately triggered when user clicks on search icon and enters something
    private fun onQueryTextListenerCallback(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(userInput: String?): Boolean {
                return checkQueryText(userInput)
            }

            override fun onQueryTextChange(userInput: String?): Boolean {
                return checkQueryText(userInput)
            }
        }
    }

    private fun checkQueryText(userInput: String?): Boolean {
        if (userInput != null && userInput.length > 1) {
            userKeyWordInput = userInput
            getKeyWordQuery(userInput)
        } else if (userInput != null && userInput == "ArticleAdapter") {
            userKeyWordInput = ""
            queryTopHeadlines()
        }
        return false
    }


    private fun getKeyWordQuery(userKeywordInput: String) {
        swipe_refresh.isRefreshing = true
        if (userKeywordInput != null && userKeywordInput.isNotEmpty()) {
            topHeadlinesObservable =
                apiEndpoint.getUserSearchInput(newsApiConfig, userKeywordInput)
            subscribeObservableOfArticle()
        } else {
            queryTopHeadlines()
        }
    }

    private fun queryTopHeadlines() {
        swipe_refresh.isRefreshing = true
        topHeadlinesObservable = apiEndpoint.getTopHeadlines("in", newsApiConfig)
        subscribeObservableOfArticle()
    }

    private fun subscribeObservableOfArticle() {
        articleList.clear()
        deleteAllDb()
        compositeDisposable.add(
            topHeadlinesObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Observable.fromIterable(it.articles)
                }
                .subscribeWith(createArticleObserver())
        )
    }

    private fun createArticleObserver(): DisposableObserver<Article> {
        return object : DisposableObserver<Article>() {
            override fun onNext(article: Article) {
                if (!articleList.contains(article)) {
                    articleList.add(article)
                }
                Log.d("NetworkData", Gson().toJson(article))

                insertDataInDB(article)
            }

            override fun onComplete() {
                showArticlesOnRecyclerView()
            }

            override fun onError(e: Throwable) {
                Log.e("createArticleObserver", "Article error: ${e.message}")
            }
        }
    }

    private fun showArticlesOnRecyclerView() {
        if (articleList.size > 0) {
            empty_text.visibility = View.GONE
            retry_fetch_button.visibility = View.GONE
            recycler_view.visibility = View.VISIBLE
            articleAdapter.setArticles(articleList)
        } else {
            recycler_view.visibility = View.GONE
            empty_text.visibility = View.VISIBLE
            retry_fetch_button.visibility = View.VISIBLE
            retry_fetch_button.setOnClickListener {
                fetchData()
            }
        }
        swipe_refresh.isRefreshing = false
    }

    private fun generateRetrofitBuilder(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            //Add RxJava2CallAdapterFactory as a Call adapter when building your Retrofit instance
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}